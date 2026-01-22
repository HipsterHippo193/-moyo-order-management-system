package com.moyo.oms.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderResponseTest {

    @Test
    void allArgsConstructor_setsAllFieldsCorrectly() {
        // Given
        Long orderId = 1L;
        Long productId = 1L;
        Integer quantity = 10;
        Long allocatedTo = 2L;
        String status = "ALLOCATED";
        String createdAt = "2026-01-20T14:30:00";

        // When
        OrderResponse response = new OrderResponse(orderId, productId, quantity, allocatedTo, status, createdAt);

        // Then
        assertEquals(orderId, response.getOrderId());
        assertEquals(productId, response.getProductId());
        assertEquals(quantity, response.getQuantity());
        assertEquals(allocatedTo, response.getAllocatedTo());
        assertEquals(status, response.getStatus());
        assertEquals(createdAt, response.getCreatedAt());
    }

    @Test
    void noArgsConstructor_createsEmptyResponse() {
        // When
        OrderResponse response = new OrderResponse();

        // Then
        assertNull(response.getOrderId());
        assertNull(response.getProductId());
        assertNull(response.getQuantity());
        assertNull(response.getAllocatedTo());
        assertNull(response.getStatus());
        assertNull(response.getCreatedAt());
    }

    @Test
    void allocatedToCanBeNull_forPendingOrders() {
        // Given
        OrderResponse response = new OrderResponse(1L, 1L, 10, null, "PENDING", "2026-01-20T14:30:00");

        // Then
        assertNull(response.getAllocatedTo());
        assertEquals("PENDING", response.getStatus());
    }

    @Test
    void setters_updateFieldsCorrectly() {
        // Given
        OrderResponse response = new OrderResponse();

        // When
        response.setOrderId(1L);
        response.setProductId(1L);
        response.setQuantity(10);
        response.setAllocatedTo(2L);
        response.setStatus("ALLOCATED");
        response.setCreatedAt("2026-01-20T14:30:00");

        // Then
        assertEquals(1L, response.getOrderId());
        assertEquals(1L, response.getProductId());
        assertEquals(10, response.getQuantity());
        assertEquals(2L, response.getAllocatedTo());
        assertEquals("ALLOCATED", response.getStatus());
        assertEquals("2026-01-20T14:30:00", response.getCreatedAt());
    }
}
