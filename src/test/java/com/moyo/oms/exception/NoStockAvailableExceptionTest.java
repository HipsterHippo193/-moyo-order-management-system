package com.moyo.oms.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NoStockAvailableExceptionTest {

    @Test
    void constructor_includesProductNameInMessage() {
        // When
        NoStockAvailableException exception = new NoStockAvailableException("Widget");

        // Then
        assertThat(exception.getMessage()).isEqualTo("No vendor has stock for product: Widget");
    }

    @Test
    void exceptionExtendsRuntimeException() {
        // When
        NoStockAvailableException exception = new NoStockAvailableException("Test Product");

        // Then
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    void constructor_handlesProductNameWithSpaces() {
        // When
        NoStockAvailableException exception = new NoStockAvailableException("Super Widget Pro");

        // Then
        assertThat(exception.getMessage()).isEqualTo("No vendor has stock for product: Super Widget Pro");
    }
}
