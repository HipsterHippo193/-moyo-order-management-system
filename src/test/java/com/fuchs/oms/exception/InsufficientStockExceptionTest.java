package com.fuchs.oms.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InsufficientStockExceptionTest {

    @Test
    void constructor_setsMessage() {
        String message = "Insufficient stock: available=5, requested=10";
        InsufficientStockException exception = new InsufficientStockException(message);

        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    void exception_isRuntimeException() {
        InsufficientStockException exception = new InsufficientStockException("test");

        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}
