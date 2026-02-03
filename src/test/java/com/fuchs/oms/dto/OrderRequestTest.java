package com.fuchs.oms.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class OrderRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validOrderRequest_passesValidation() {
        // Given
        OrderRequest request = new OrderRequest(1L, 10);

        // When
        Set<ConstraintViolation<OrderRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void nullProductId_failsValidation() {
        // Given
        OrderRequest request = new OrderRequest(null, 10);

        // When
        Set<ConstraintViolation<OrderRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().equals("Product ID is required")));
    }

    @Test
    void nullQuantity_failsValidation() {
        // Given
        OrderRequest request = new OrderRequest(1L, null);

        // When
        Set<ConstraintViolation<OrderRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().equals("Quantity is required")));
    }

    @Test
    void zeroQuantity_failsValidation() {
        // Given
        OrderRequest request = new OrderRequest(1L, 0);

        // When
        Set<ConstraintViolation<OrderRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().equals("Quantity must be at least 1")));
    }

    @Test
    void negativeQuantity_failsValidation() {
        // Given
        OrderRequest request = new OrderRequest(1L, -5);

        // When
        Set<ConstraintViolation<OrderRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().equals("Quantity must be at least 1")));
    }

    @Test
    void quantityOfOne_passesValidation() {
        // Given
        OrderRequest request = new OrderRequest(1L, 1);

        // When
        Set<ConstraintViolation<OrderRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void largeQuantity_passesValidation() {
        // Given
        OrderRequest request = new OrderRequest(1L, 1000000);

        // When
        Set<ConstraintViolation<OrderRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void noArgsConstructor_createsEmptyRequest() {
        // When
        OrderRequest request = new OrderRequest();

        // Then
        assertNull(request.getProductId());
        assertNull(request.getQuantity());
    }

    @Test
    void setters_updateFieldsCorrectly() {
        // Given
        OrderRequest request = new OrderRequest();

        // When
        request.setProductId(5L);
        request.setQuantity(25);

        // Then
        assertEquals(5L, request.getProductId());
        assertEquals(25, request.getQuantity());
    }
}
