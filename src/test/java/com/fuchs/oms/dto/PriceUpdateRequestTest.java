package com.fuchs.oms.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PriceUpdateRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidationWithValidPrice() {
        PriceUpdateRequest request = new PriceUpdateRequest(new BigDecimal("45.00"));

        Set<ConstraintViolation<PriceUpdateRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldPassValidationWithMinimumValidPrice() {
        PriceUpdateRequest request = new PriceUpdateRequest(new BigDecimal("0.01"));

        Set<ConstraintViolation<PriceUpdateRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailValidationWithNullPrice() {
        PriceUpdateRequest request = new PriceUpdateRequest(null);

        Set<ConstraintViolation<PriceUpdateRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Price is required");
    }

    @Test
    void shouldFailValidationWithZeroPrice() {
        PriceUpdateRequest request = new PriceUpdateRequest(new BigDecimal("0.00"));

        Set<ConstraintViolation<PriceUpdateRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Price must be greater than zero");
    }

    @Test
    void shouldFailValidationWithNegativePrice() {
        PriceUpdateRequest request = new PriceUpdateRequest(new BigDecimal("-10.00"));

        Set<ConstraintViolation<PriceUpdateRequest>> violations = validator.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Price must be greater than zero");
    }

    @Test
    void shouldCreatePriceUpdateRequestWithNoArgsConstructorAndSetter() {
        PriceUpdateRequest request = new PriceUpdateRequest();
        request.setPrice(new BigDecimal("55.00"));

        assertThat(request.getPrice()).isEqualTo(new BigDecimal("55.00"));
    }

    @Test
    void shouldHaveCorrectEqualsAndHashCode() {
        PriceUpdateRequest request1 = new PriceUpdateRequest(new BigDecimal("45.00"));
        PriceUpdateRequest request2 = new PriceUpdateRequest(new BigDecimal("45.00"));

        assertThat(request1).isEqualTo(request2);
        assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
    }
}
