package com.fuchs.oms.dto;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderResponseTest {

    @Test
    void allArgsConstructor_setsAllFieldsCorrectly() {
        // Given
        Long orderId = 1L;
        Long productId = 1L;
        String productName = "Widget";
        Integer quantity = 10;
        Long allocatedVendorId = 2L;
        String allocatedVendorName = "Vendor Beta";
        BigDecimal price = new BigDecimal("45.00");
        BigDecimal totalPrice = new BigDecimal("450.00");
        String status = "ALLOCATED";
        String createdAt = "2026-01-20T14:30:00";

        // When
        OrderResponse response = new OrderResponse(orderId, productId, productName, quantity,
            allocatedVendorId, allocatedVendorName, price, totalPrice, status, createdAt);

        // Then
        assertEquals(orderId, response.getOrderId());
        assertEquals(productId, response.getProductId());
        assertEquals(productName, response.getProductName());
        assertEquals(quantity, response.getQuantity());
        assertEquals(allocatedVendorId, response.getAllocatedVendorId());
        assertEquals(allocatedVendorName, response.getAllocatedVendorName());
        assertEquals(price, response.getPrice());
        assertEquals(totalPrice, response.getTotalPrice());
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
        assertNull(response.getProductName());
        assertNull(response.getQuantity());
        assertNull(response.getAllocatedVendorId());
        assertNull(response.getAllocatedVendorName());
        assertNull(response.getPrice());
        assertNull(response.getTotalPrice());
        assertNull(response.getStatus());
        assertNull(response.getCreatedAt());
    }

    @Test
    void allocatedVendorIdCanBeNull_forPendingOrders() {
        // Given
        OrderResponse response = new OrderResponse(1L, 1L, "Widget", 10, null, null,
            null, null, "PENDING", "2026-01-20T14:30:00");

        // Then
        assertNull(response.getAllocatedVendorId());
        assertNull(response.getAllocatedVendorName());
        assertEquals("PENDING", response.getStatus());
    }

    @Test
    void setters_updateFieldsCorrectly() {
        // Given
        OrderResponse response = new OrderResponse();

        // When
        response.setOrderId(1L);
        response.setProductId(1L);
        response.setProductName("Widget");
        response.setQuantity(10);
        response.setAllocatedVendorId(2L);
        response.setAllocatedVendorName("Vendor Beta");
        response.setPrice(new BigDecimal("45.00"));
        response.setTotalPrice(new BigDecimal("450.00"));
        response.setStatus("ALLOCATED");
        response.setCreatedAt("2026-01-20T14:30:00");

        // Then
        assertEquals(1L, response.getOrderId());
        assertEquals(1L, response.getProductId());
        assertEquals("Widget", response.getProductName());
        assertEquals(10, response.getQuantity());
        assertEquals(2L, response.getAllocatedVendorId());
        assertEquals("Vendor Beta", response.getAllocatedVendorName());
        assertEquals(new BigDecimal("45.00"), response.getPrice());
        assertEquals(new BigDecimal("450.00"), response.getTotalPrice());
        assertEquals("ALLOCATED", response.getStatus());
        assertEquals("2026-01-20T14:30:00", response.getCreatedAt());
    }
}
