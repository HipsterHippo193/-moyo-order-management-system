package com.moyo.oms.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class StockUpdateRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldFailValidationWithNullStock() {
        StockUpdateRequest request = new StockUpdateRequest(null);

        Set<ConstraintViolation<StockUpdateRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Stock is required");
    }

    @Test
    void shouldFailValidationWithNegativeStock() {
        StockUpdateRequest request = new StockUpdateRequest(-5);

        Set<ConstraintViolation<StockUpdateRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Stock cannot be negative");
    }

    @Test
    void shouldPassValidationWithZeroStock() {
        StockUpdateRequest request = new StockUpdateRequest(0);

        Set<ConstraintViolation<StockUpdateRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldPassValidationWithPositiveStock() {
        StockUpdateRequest request = new StockUpdateRequest(100);

        Set<ConstraintViolation<StockUpdateRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldCreateStockUpdateRequestWithNoArgsConstructorAndSetter() {
        StockUpdateRequest request = new StockUpdateRequest();
        request.setStock(50);

        assertThat(request.getStock()).isEqualTo(50);
    }

    @Test
    void shouldHaveCorrectEqualsAndHashCode() {
        StockUpdateRequest request1 = new StockUpdateRequest(100);
        StockUpdateRequest request2 = new StockUpdateRequest(100);

        assertThat(request1).isEqualTo(request2);
        assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
    }
}
