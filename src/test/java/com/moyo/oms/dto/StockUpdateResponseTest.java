package com.moyo.oms.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StockUpdateResponseTest {

    @Test
    void shouldCreateStockUpdateResponseWithAllArgsConstructor() {
        StockUpdateResponse response = new StockUpdateResponse(
            1L, "widget-001", "Widget", 150, "2026-01-19T14:30:00.000Z"
        );

        assertThat(response.getProductId()).isEqualTo(1L);
        assertThat(response.getProductCode()).isEqualTo("widget-001");
        assertThat(response.getName()).isEqualTo("Widget");
        assertThat(response.getNewStock()).isEqualTo(150);
        assertThat(response.getUpdatedAt()).isEqualTo("2026-01-19T14:30:00.000Z");
    }

    @Test
    void shouldCreateStockUpdateResponseWithNoArgsConstructorAndSetters() {
        StockUpdateResponse response = new StockUpdateResponse();
        response.setProductId(2L);
        response.setProductCode("gadget-002");
        response.setName("Gadget");
        response.setNewStock(200);
        response.setUpdatedAt("2026-01-19T15:00:00.000Z");

        assertThat(response.getProductId()).isEqualTo(2L);
        assertThat(response.getProductCode()).isEqualTo("gadget-002");
        assertThat(response.getName()).isEqualTo("Gadget");
        assertThat(response.getNewStock()).isEqualTo(200);
        assertThat(response.getUpdatedAt()).isEqualTo("2026-01-19T15:00:00.000Z");
    }

    @Test
    void shouldHaveCorrectEqualsAndHashCode() {
        StockUpdateResponse response1 = new StockUpdateResponse(
            1L, "widget-001", "Widget", 150, "2026-01-19T14:30:00.000Z"
        );
        StockUpdateResponse response2 = new StockUpdateResponse(
            1L, "widget-001", "Widget", 150, "2026-01-19T14:30:00.000Z"
        );

        assertThat(response1).isEqualTo(response2);
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
    }

    @Test
    void shouldHaveCorrectToString() {
        StockUpdateResponse response = new StockUpdateResponse(
            1L, "widget-001", "Widget", 150, "2026-01-19T14:30:00.000Z"
        );

        String toString = response.toString();
        assertThat(toString).contains("productId=1");
        assertThat(toString).contains("productCode=widget-001");
        assertThat(toString).contains("name=Widget");
        assertThat(toString).contains("newStock=150");
        assertThat(toString).contains("updatedAt=2026-01-19T14:30:00.000Z");
    }
}
