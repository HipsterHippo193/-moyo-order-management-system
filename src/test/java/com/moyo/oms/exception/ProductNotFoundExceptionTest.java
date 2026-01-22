package com.moyo.oms.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductNotFoundExceptionTest {

    @Test
    void constructor_setsMessage() {
        // Given
        String message = "Product not found: productId=999";

        // When
        ProductNotFoundException exception = new ProductNotFoundException(message);

        // Then
        assertEquals(message, exception.getMessage());
    }

    @Test
    void exception_extendsRuntimeException() {
        // Given
        ProductNotFoundException exception = new ProductNotFoundException("test");

        // Then
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void exception_canBeThrown() {
        // Then
        assertThrows(ProductNotFoundException.class, () -> {
            throw new ProductNotFoundException("Product not found");
        });
    }
}
