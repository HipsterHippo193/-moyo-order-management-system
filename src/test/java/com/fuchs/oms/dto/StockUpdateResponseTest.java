package com.fuchs.oms.dto;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class StockUpdateResponseTest {

    @Test
    void shouldCreateStockUpdateResponseWithAllArgsConstructor() {
        StockUpdateResponse response = new StockUpdateResponse(
            1L, "widget-001", "Widget", 2L, "Vendor Beta",
            100, 150, new BigDecimal("55.00"), "2026-01-19T14:30:00.000Z"
        );

        assertThat(response.getProductId()).isEqualTo(1L);
        assertThat(response.getProductCode()).isEqualTo("widget-001");
        assertThat(response.getProductName()).isEqualTo("Widget");
        assertThat(response.getVendorId()).isEqualTo(2L);
        assertThat(response.getVendorName()).isEqualTo("Vendor Beta");
        assertThat(response.getOldStock()).isEqualTo(100);
        assertThat(response.getNewStock()).isEqualTo(150);
        assertThat(response.getCurrentPrice()).isEqualTo(new BigDecimal("55.00"));
        assertThat(response.getUpdatedAt()).isEqualTo("2026-01-19T14:30:00.000Z");
    }

    @Test
    void shouldCreateStockUpdateResponseWithNoArgsConstructorAndSetters() {
        StockUpdateResponse response = new StockUpdateResponse();
        response.setProductId(2L);
        response.setProductCode("gadget-002");
        response.setProductName("Gadget");
        response.setVendorId(3L);
        response.setVendorName("Vendor Gamma");
        response.setOldStock(50);
        response.setNewStock(200);
        response.setCurrentPrice(new BigDecimal("75.50"));
        response.setUpdatedAt("2026-01-19T15:00:00.000Z");

        assertThat(response.getProductId()).isEqualTo(2L);
        assertThat(response.getProductCode()).isEqualTo("gadget-002");
        assertThat(response.getProductName()).isEqualTo("Gadget");
        assertThat(response.getVendorId()).isEqualTo(3L);
        assertThat(response.getVendorName()).isEqualTo("Vendor Gamma");
        assertThat(response.getOldStock()).isEqualTo(50);
        assertThat(response.getNewStock()).isEqualTo(200);
        assertThat(response.getCurrentPrice()).isEqualTo(new BigDecimal("75.50"));
        assertThat(response.getUpdatedAt()).isEqualTo("2026-01-19T15:00:00.000Z");
    }

    @Test
    void shouldHaveCorrectEqualsAndHashCode() {
        StockUpdateResponse response1 = new StockUpdateResponse(
            1L, "widget-001", "Widget", 2L, "Vendor Beta",
            100, 150, new BigDecimal("55.00"), "2026-01-19T14:30:00.000Z"
        );
        StockUpdateResponse response2 = new StockUpdateResponse(
            1L, "widget-001", "Widget", 2L, "Vendor Beta",
            100, 150, new BigDecimal("55.00"), "2026-01-19T14:30:00.000Z"
        );

        assertThat(response1).isEqualTo(response2);
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
    }

    @Test
    void shouldHaveCorrectToString() {
        StockUpdateResponse response = new StockUpdateResponse(
            1L, "widget-001", "Widget", 2L, "Vendor Beta",
            100, 150, new BigDecimal("55.00"), "2026-01-19T14:30:00.000Z"
        );

        String toString = response.toString();
        assertThat(toString).contains("productId=1");
        assertThat(toString).contains("productCode=widget-001");
        assertThat(toString).contains("productName=Widget");
        assertThat(toString).contains("vendorId=2");
        assertThat(toString).contains("vendorName=Vendor Beta");
        assertThat(toString).contains("oldStock=100");
        assertThat(toString).contains("newStock=150");
        assertThat(toString).contains("currentPrice=55.00");
        assertThat(toString).contains("updatedAt=2026-01-19T14:30:00.000Z");
    }
}
