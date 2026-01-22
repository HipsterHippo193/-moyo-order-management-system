package com.moyo.oms.dto;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PriceUpdateResponseTest {

    @Test
    void shouldCreatePriceUpdateResponseWithAllArgsConstructor() {
        PriceUpdateResponse response = new PriceUpdateResponse(
            1L, "widget-001", "Widget", new BigDecimal("55.00"), "2026-01-19T14:30:00.000Z"
        );

        assertThat(response.getProductId()).isEqualTo(1L);
        assertThat(response.getProductCode()).isEqualTo("widget-001");
        assertThat(response.getName()).isEqualTo("Widget");
        assertThat(response.getNewPrice()).isEqualTo(new BigDecimal("55.00"));
        assertThat(response.getUpdatedAt()).isEqualTo("2026-01-19T14:30:00.000Z");
    }

    @Test
    void shouldCreatePriceUpdateResponseWithNoArgsConstructorAndSetters() {
        PriceUpdateResponse response = new PriceUpdateResponse();
        response.setProductId(2L);
        response.setProductCode("gadget-002");
        response.setName("Gadget");
        response.setNewPrice(new BigDecimal("75.50"));
        response.setUpdatedAt("2026-01-19T15:00:00.000Z");

        assertThat(response.getProductId()).isEqualTo(2L);
        assertThat(response.getProductCode()).isEqualTo("gadget-002");
        assertThat(response.getName()).isEqualTo("Gadget");
        assertThat(response.getNewPrice()).isEqualTo(new BigDecimal("75.50"));
        assertThat(response.getUpdatedAt()).isEqualTo("2026-01-19T15:00:00.000Z");
    }

    @Test
    void shouldHaveCorrectEqualsAndHashCode() {
        PriceUpdateResponse response1 = new PriceUpdateResponse(
            1L, "widget-001", "Widget", new BigDecimal("55.00"), "2026-01-19T14:30:00.000Z"
        );
        PriceUpdateResponse response2 = new PriceUpdateResponse(
            1L, "widget-001", "Widget", new BigDecimal("55.00"), "2026-01-19T14:30:00.000Z"
        );

        assertThat(response1).isEqualTo(response2);
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
    }

    @Test
    void shouldHaveCorrectToString() {
        PriceUpdateResponse response = new PriceUpdateResponse(
            1L, "widget-001", "Widget", new BigDecimal("55.00"), "2026-01-19T14:30:00.000Z"
        );

        String toString = response.toString();
        assertThat(toString).contains("productId=1");
        assertThat(toString).contains("productCode=widget-001");
        assertThat(toString).contains("name=Widget");
        assertThat(toString).contains("newPrice=55.00");
        assertThat(toString).contains("updatedAt=2026-01-19T14:30:00.000Z");
    }
}
