package com.moyo.oms.exception;

import com.moyo.oms.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleInsufficientStock_returns400BadRequest() {
        InsufficientStockException exception = new InsufficientStockException(
            "Insufficient stock: available=5, requested=10");

        ResponseEntity<ErrorResponse> response = handler.handleInsufficientStock(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void handleInsufficientStock_returnsCorrectErrorFormat() {
        String errorMessage = "Insufficient stock: available=5, requested=10";
        InsufficientStockException exception = new InsufficientStockException(errorMessage);

        ResponseEntity<ErrorResponse> response = handler.handleInsufficientStock(exception);
        ErrorResponse body = response.getBody();

        assertThat(body).isNotNull();
        assertThat(body.getError()).isEqualTo(errorMessage);
        assertThat(body.getStatus()).isEqualTo(400);
        assertThat(body.getTimestamp()).isNotNull();
    }

    @Test
    void handleResourceNotFound_returns404NotFound() {
        ResourceNotFoundException exception = new ResourceNotFoundException(
            "Product not found for vendor: vendorId=1, productId=999");

        ResponseEntity<ErrorResponse> response = handler.handleResourceNotFound(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void handleVendorAccessDenied_returns403Forbidden() {
        VendorAccessDeniedException exception = new VendorAccessDeniedException("Access denied");

        ResponseEntity<ErrorResponse> response = handler.handleVendorAccessDenied(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
