package com.fuchs.oms.repository;

import com.fuchs.oms.model.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void save_persistsOrderWithAllFields() {
        // Given
        Order order = new Order();
        order.setProductId(1L);
        order.setQuantity(10);
        order.setAllocatedVendorId(2L);
        order.setStatus("ALLOCATED");

        // When
        Order saved = orderRepository.save(order);

        // Then
        assertNotNull(saved.getId());
        assertEquals(1L, saved.getProductId());
        assertEquals(10, saved.getQuantity());
        assertEquals(2L, saved.getAllocatedVendorId());
        assertEquals("ALLOCATED", saved.getStatus());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void save_orderWithNullAllocatedVendor_persists() {
        // Given
        Order order = new Order();
        order.setProductId(1L);
        order.setQuantity(10);
        order.setStatus("PENDING");
        order.setAllocatedVendorId(null);

        // When
        Order saved = orderRepository.save(order);

        // Then
        assertNotNull(saved.getId());
        assertNull(saved.getAllocatedVendorId());
    }

    @Test
    void findById_existingOrder_returnsOrder() {
        // Given
        Order order = new Order();
        order.setProductId(1L);
        order.setQuantity(10);
        order.setStatus("ALLOCATED");
        Order saved = orderRepository.save(order);

        // When
        Order found = orderRepository.findById(saved.getId()).orElse(null);

        // Then
        assertNotNull(found);
        assertEquals(saved.getId(), found.getId());
    }

    @Test
    void findByAllocatedVendorIdOrderByCreatedAtDesc_returnsOrdersSortedByCreatedAt() {
        // Given - create orders with different timestamps
        Order order1 = new Order();
        order1.setProductId(1L);
        order1.setQuantity(10);
        order1.setAllocatedVendorId(2L);
        order1.setStatus("ALLOCATED");
        orderRepository.save(order1);

        Order order2 = new Order();
        order2.setProductId(1L);
        order2.setQuantity(20);
        order2.setAllocatedVendorId(2L);
        order2.setStatus("ALLOCATED");
        orderRepository.save(order2);

        Order order3 = new Order();
        order3.setProductId(1L);
        order3.setQuantity(30);
        order3.setAllocatedVendorId(3L);  // Different vendor
        order3.setStatus("ALLOCATED");
        orderRepository.save(order3);

        // When
        List<Order> vendor2Orders = orderRepository.findByAllocatedVendorIdOrderByCreatedAtDesc(2L);

        // Then
        assertEquals(2, vendor2Orders.size());
        // Orders should be sorted by createdAt descending (newest first)
        assertTrue(vendor2Orders.get(0).getCreatedAt().isAfter(vendor2Orders.get(1).getCreatedAt()) ||
                   vendor2Orders.get(0).getCreatedAt().isEqual(vendor2Orders.get(1).getCreatedAt()));
    }

    @Test
    void findByAllocatedVendorIdOrderByCreatedAtDesc_noOrdersForVendor_returnsEmptyList() {
        // Given - no orders for vendor 999

        // When
        List<Order> orders = orderRepository.findByAllocatedVendorIdOrderByCreatedAtDesc(999L);

        // Then
        assertTrue(orders.isEmpty());
    }

    @Test
    void prePersist_setsCreatedAtAutomatically() {
        // Given
        Order order = new Order();
        order.setProductId(1L);
        order.setQuantity(10);
        order.setStatus("PENDING");

        // When
        Order saved = orderRepository.save(order);

        // Then
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void prePersist_setsDefaultStatusToPending() {
        // Given
        Order order = new Order();
        order.setProductId(1L);
        order.setQuantity(10);
        // status not set

        // When
        Order saved = orderRepository.save(order);

        // Then
        assertEquals("PENDING", saved.getStatus());
    }
}
