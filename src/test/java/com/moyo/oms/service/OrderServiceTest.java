package com.moyo.oms.service;

import com.moyo.oms.dto.OrderRequest;
import com.moyo.oms.dto.OrderResponse;
import com.moyo.oms.exception.NoStockAvailableException;
import com.moyo.oms.exception.ProductNotFoundException;
import com.moyo.oms.exception.ResourceNotFoundException;
import com.moyo.oms.model.Order;
import com.moyo.oms.model.Product;
import com.moyo.oms.repository.OrderRepository;
import com.moyo.oms.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private AllocationService allocationService;

    @InjectMocks
    private OrderService orderService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setProductCode("widget-001");
        testProduct.setName("Widget");
    }

    @Test
    void createOrder_withValidData_returnsOrderResponse() {
        // Given
        OrderRequest request = new OrderRequest(1L, 10);

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(allocationService.allocate(1L, 10))
            .thenReturn(new AllocationService.AllocationResult(2L, true));

        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setProductId(1L);
        savedOrder.setQuantity(10);
        savedOrder.setAllocatedVendorId(2L);
        savedOrder.setStatus("ALLOCATED");
        savedOrder.setCreatedAt(LocalDateTime.now());

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // When
        OrderResponse response = orderService.createOrder(request);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getOrderId());
        assertEquals(1L, response.getProductId());
        assertEquals(10, response.getQuantity());
        assertEquals(2L, response.getAllocatedTo());
        assertEquals("ALLOCATED", response.getStatus());
        assertNotNull(response.getCreatedAt());
    }

    @Test
    void createOrder_withNonExistentProduct_throwsProductNotFoundException() {
        // Given
        OrderRequest request = new OrderRequest(999L, 10);
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class,
            () -> orderService.createOrder(request));

        assertEquals("Product not found: productId=999", exception.getMessage());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_savesOrderToRepository() {
        // Given
        OrderRequest request = new OrderRequest(1L, 10);

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(allocationService.allocate(1L, 10))
            .thenReturn(new AllocationService.AllocationResult(2L, true));

        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setProductId(1L);
        savedOrder.setQuantity(10);
        savedOrder.setAllocatedVendorId(2L);
        savedOrder.setStatus("ALLOCATED");
        savedOrder.setCreatedAt(LocalDateTime.now());

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // When
        orderService.createOrder(request);

        // Then
        verify(orderRepository).save(any(Order.class));
    }

    // ==================== FR13: No Stock Error Handling Tests ====================

    @Test
    void createOrder_withNoVendorStock_throwsNoStockAvailableException() {
        // Given - All vendors have zero stock (allocation returns failure)
        OrderRequest request = new OrderRequest(1L, 10);

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(allocationService.allocate(1L, 10))
            .thenReturn(new AllocationService.AllocationResult(null, false));

        // When/Then
        assertThatThrownBy(() -> orderService.createOrder(request))
            .isInstanceOf(NoStockAvailableException.class)
            .hasMessage("No vendor has stock for product: Widget");

        // Verify no order was saved
        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_withQuantityExceedingAllStock_throwsNoStockAvailableException() {
        // Given - Quantity exceeds all vendors' available stock
        OrderRequest request = new OrderRequest(1L, 1000);

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(allocationService.allocate(1L, 1000))
            .thenReturn(new AllocationService.AllocationResult(null, false));

        // When/Then
        assertThatThrownBy(() -> orderService.createOrder(request))
            .isInstanceOf(NoStockAvailableException.class)
            .hasMessage("No vendor has stock for product: Widget");

        // Verify no order was saved
        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_withAllocationFailure_doesNotSaveOrder() {
        // Given
        OrderRequest request = new OrderRequest(1L, 50);

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(allocationService.allocate(1L, 50))
            .thenReturn(new AllocationService.AllocationResult(null, false));

        // When
        try {
            orderService.createOrder(request);
            fail("Expected NoStockAvailableException to be thrown");
        } catch (NoStockAvailableException e) {
            // Expected
        }

        // Then - Verify orderRepository.save() was NEVER called
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_callsAllocationServiceWithCorrectParams() {
        // Given
        OrderRequest request = new OrderRequest(1L, 25);

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(allocationService.allocate(1L, 25))
            .thenReturn(new AllocationService.AllocationResult(2L, true));

        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setProductId(1L);
        savedOrder.setQuantity(25);
        savedOrder.setAllocatedVendorId(2L);
        savedOrder.setStatus("ALLOCATED");
        savedOrder.setCreatedAt(LocalDateTime.now());

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // When
        orderService.createOrder(request);

        // Then
        verify(allocationService).allocate(1L, 25);
    }

    // ==================== Story 4B.1: Get Vendor Orders Tests ====================

    @Test
    void getVendorOrders_withOrders_returnsMappedOrderResponses() {
        // Given - Vendor 2 has two orders
        Long vendorId = 2L;

        Order order1 = new Order();
        order1.setId(1L);
        order1.setProductId(1L);
        order1.setQuantity(10);
        order1.setAllocatedVendorId(vendorId);
        order1.setStatus("ALLOCATED");
        order1.setCreatedAt(LocalDateTime.of(2026, 1, 20, 14, 0, 0));

        Order order2 = new Order();
        order2.setId(2L);
        order2.setProductId(1L);
        order2.setQuantity(5);
        order2.setAllocatedVendorId(vendorId);
        order2.setStatus("ALLOCATED");
        order2.setCreatedAt(LocalDateTime.of(2026, 1, 20, 15, 0, 0));

        when(orderRepository.findByAllocatedVendorIdOrderByCreatedAtDesc(vendorId))
            .thenReturn(Arrays.asList(order2, order1)); // Newest first

        // When
        List<OrderResponse> responses = orderService.getVendorOrders(vendorId);

        // Then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getOrderId()).isEqualTo(2L);
        assertThat(responses.get(0).getProductId()).isEqualTo(1L);
        assertThat(responses.get(0).getQuantity()).isEqualTo(5);
        assertThat(responses.get(0).getAllocatedTo()).isEqualTo(vendorId);
        assertThat(responses.get(0).getStatus()).isEqualTo("ALLOCATED");
        assertThat(responses.get(1).getOrderId()).isEqualTo(1L);
    }

    @Test
    void getVendorOrders_withNoOrders_returnsEmptyList() {
        // Given - Vendor 3 has no orders allocated
        Long vendorId = 3L;

        when(orderRepository.findByAllocatedVendorIdOrderByCreatedAtDesc(vendorId))
            .thenReturn(Collections.emptyList());

        // When
        List<OrderResponse> responses = orderService.getVendorOrders(vendorId);

        // Then
        assertThat(responses).isEmpty();
        verify(orderRepository).findByAllocatedVendorIdOrderByCreatedAtDesc(vendorId);
    }

    @Test
    void getVendorOrders_ordersAreSortedByCreatedAtDesc() {
        // Given - Orders returned in descending order (newest first)
        Long vendorId = 2L;

        LocalDateTime older = LocalDateTime.of(2026, 1, 20, 10, 0, 0);
        LocalDateTime middle = LocalDateTime.of(2026, 1, 20, 12, 0, 0);
        LocalDateTime newest = LocalDateTime.of(2026, 1, 20, 14, 0, 0);

        Order oldOrder = new Order();
        oldOrder.setId(1L);
        oldOrder.setProductId(1L);
        oldOrder.setQuantity(10);
        oldOrder.setAllocatedVendorId(vendorId);
        oldOrder.setStatus("ALLOCATED");
        oldOrder.setCreatedAt(older);

        Order middleOrder = new Order();
        middleOrder.setId(2L);
        middleOrder.setProductId(1L);
        middleOrder.setQuantity(15);
        middleOrder.setAllocatedVendorId(vendorId);
        middleOrder.setStatus("ALLOCATED");
        middleOrder.setCreatedAt(middle);

        Order newOrder = new Order();
        newOrder.setId(3L);
        newOrder.setProductId(1L);
        newOrder.setQuantity(20);
        newOrder.setAllocatedVendorId(vendorId);
        newOrder.setStatus("ALLOCATED");
        newOrder.setCreatedAt(newest);

        // Repository returns in DESC order (newest first)
        when(orderRepository.findByAllocatedVendorIdOrderByCreatedAtDesc(vendorId))
            .thenReturn(Arrays.asList(newOrder, middleOrder, oldOrder));

        // When
        List<OrderResponse> responses = orderService.getVendorOrders(vendorId);

        // Then - Verify order is maintained (newest first)
        assertThat(responses).hasSize(3);
        assertThat(responses.get(0).getOrderId()).isEqualTo(3L); // Newest
        assertThat(responses.get(1).getOrderId()).isEqualTo(2L); // Middle
        assertThat(responses.get(2).getOrderId()).isEqualTo(1L); // Oldest

        // Verify createdAt order
        assertThat(responses.get(0).getCreatedAt()).contains("2026-01-20T14:00");
        assertThat(responses.get(1).getCreatedAt()).contains("2026-01-20T12:00");
        assertThat(responses.get(2).getCreatedAt()).contains("2026-01-20T10:00");
    }

    // ==================== Story 4B.2: Get Order By ID Tests ====================

    @Test
    void getOrderById_withValidOrderAndVendor_returnsOrderResponse() {
        // Given - Order exists and belongs to the vendor
        Long orderId = 1L;
        Long vendorId = 2L;

        Order order = new Order();
        order.setId(orderId);
        order.setProductId(1L);
        order.setQuantity(10);
        order.setAllocatedVendorId(vendorId);
        order.setStatus("ALLOCATED");
        order.setCreatedAt(LocalDateTime.of(2026, 1, 20, 12, 0, 0));

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // When
        OrderResponse response = orderService.getOrderById(orderId, vendorId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getOrderId()).isEqualTo(orderId);
        assertThat(response.getProductId()).isEqualTo(1L);
        assertThat(response.getQuantity()).isEqualTo(10);
        assertThat(response.getAllocatedTo()).isEqualTo(vendorId);
        assertThat(response.getStatus()).isEqualTo("ALLOCATED");
        assertThat(response.getCreatedAt()).contains("2026-01-20T12:00");
        verify(orderRepository).findById(orderId);
    }

    @Test
    void getOrderById_withNonExistentOrder_throwsResourceNotFoundException() {
        // Given - Order does not exist
        Long orderId = 999L;
        Long vendorId = 2L;

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> orderService.getOrderById(orderId, vendorId))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Order not found: orderId=" + orderId);

        verify(orderRepository).findById(orderId);
    }

    @Test
    void getOrderById_withOtherVendorOrder_throwsResourceNotFoundException() {
        // Given - Order exists but belongs to different vendor (security test)
        Long orderId = 1L;
        Long requestingVendorId = 1L; // Vendor A trying to access
        Long actualVendorId = 2L; // Order belongs to Vendor B

        Order order = new Order();
        order.setId(orderId);
        order.setProductId(1L);
        order.setQuantity(10);
        order.setAllocatedVendorId(actualVendorId); // Order belongs to Vendor B
        order.setStatus("ALLOCATED");
        order.setCreatedAt(LocalDateTime.now());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // When/Then - Should return same error as non-existent order (security)
        assertThatThrownBy(() -> orderService.getOrderById(orderId, requestingVendorId))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Order not found: orderId=" + orderId);

        verify(orderRepository).findById(orderId);
    }
}
