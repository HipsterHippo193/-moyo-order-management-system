package com.fuchs.oms.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void orderEntityCreation_withAllFields_setsFieldsCorrectly() {
        // Given
        Long id = 1L;
        Long productId = 1L;
        Integer quantity = 10;
        Long allocatedVendorId = 2L;
        String status = "ALLOCATED";
        LocalDateTime createdAt = LocalDateTime.now();

        // When
        Order order = new Order(id, productId, quantity, allocatedVendorId, status, createdAt);

        // Then
        assertEquals(id, order.getId());
        assertEquals(productId, order.getProductId());
        assertEquals(quantity, order.getQuantity());
        assertEquals(allocatedVendorId, order.getAllocatedVendorId());
        assertEquals(status, order.getStatus());
        assertEquals(createdAt, order.getCreatedAt());
    }

    @Test
    void orderEntityCreation_withNoArgsConstructor_createsEmptyOrder() {
        // When
        Order order = new Order();

        // Then
        assertNull(order.getId());
        assertNull(order.getProductId());
        assertNull(order.getQuantity());
        assertNull(order.getAllocatedVendorId());
        assertNull(order.getCreatedAt());
    }

    @Test
    void orderEntitySetters_updateFieldsCorrectly() {
        // Given
        Order order = new Order();

        // When
        order.setId(1L);
        order.setProductId(1L);
        order.setQuantity(10);
        order.setAllocatedVendorId(2L);
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());

        // Then
        assertEquals(1L, order.getId());
        assertEquals(1L, order.getProductId());
        assertEquals(10, order.getQuantity());
        assertEquals(2L, order.getAllocatedVendorId());
        assertEquals("PENDING", order.getStatus());
        assertNotNull(order.getCreatedAt());
    }

    @Test
    void orderEntity_allocatedVendorIdCanBeNull() {
        // Given
        Order order = new Order();
        order.setProductId(1L);
        order.setQuantity(10);
        order.setStatus("PENDING");

        // When
        order.setAllocatedVendorId(null);

        // Then
        assertNull(order.getAllocatedVendorId());
    }

    @Test
    void orderEntity_statusDefaultValues() {
        // Given
        Order order = new Order();

        // When - status not set explicitly

        // Then - status can be null before persistence
        assertNull(order.getStatus());
    }
}
