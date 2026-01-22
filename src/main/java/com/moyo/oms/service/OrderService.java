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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final AllocationService allocationService;

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        // 1. Validate product exists and get product for error message
        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new ProductNotFoundException(
                "Product not found: productId=" + request.getProductId()));

        // 2. Attempt allocation FIRST (before creating order)
        AllocationService.AllocationResult result = allocationService.allocate(
            request.getProductId(),
            request.getQuantity()
        );

        // 3. Handle allocation failure - throw exception, do NOT save order (FR13)
        if (!result.success()) {
            throw new NoStockAvailableException(product.getName());
        }

        // 4. Only create and save order if allocation succeeded
        Order order = new Order();
        order.setProductId(request.getProductId());
        order.setQuantity(request.getQuantity());
        order.setAllocatedVendorId(result.vendorId());
        order.setStatus("ALLOCATED");

        // 5. Save and return
        Order saved = orderRepository.save(order);
        return toOrderResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getVendorOrders(Long vendorId) {
        List<Order> orders = orderRepository.findByAllocatedVendorIdOrderByCreatedAtDesc(vendorId);
        return orders.stream()
            .map(this::toOrderResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId, Long vendorId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found: orderId=" + orderId));

        // Security: Return same error if order belongs to another vendor
        if (!order.getAllocatedVendorId().equals(vendorId)) {
            throw new ResourceNotFoundException("Order not found: orderId=" + orderId);
        }

        return toOrderResponse(order);
    }

    private OrderResponse toOrderResponse(Order order) {
        return new OrderResponse(
            order.getId(),
            order.getProductId(),
            order.getQuantity(),
            order.getAllocatedVendorId(),
            order.getStatus(),
            order.getCreatedAt() != null
                ? order.getCreatedAt().toString()
                : Instant.now().toString()
        );
    }
}
