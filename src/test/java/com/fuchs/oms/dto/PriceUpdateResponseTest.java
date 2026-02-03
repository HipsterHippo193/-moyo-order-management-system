package com.fuchs.oms.dto;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PriceUpdateResponseTest {

    @Test
    void shouldCreatePriceUpdateResponseWithAllArgsConstructor() {
        PriceUpdateResponse response = new PriceUpdateResponse(
            1L, "widget-001", "Widget", 2L, "Vendor Beta",
            new BigDecimal("50.00"), new BigDecimal("55.00"), 100, "2026-01-19T14:30:00.000Z"
        );

        assertThat(response.getProductId()).isEqualTo(1L);
        assertThat(response.getProductCode()).isEqualTo("widget-001");
        assertThat(response.getProductName()).isEqualTo("Widget");
        assertThat(response.getVendorId()).isEqualTo(2L);
        assertThat(response.getVendorName()).isEqualTo("Vendor Beta");
        assertThat(response.getOldPrice()).isEqualTo(new BigDecimal("50.00"));
        assertThat(response.getNewPrice()).isEqualTo(new BigDecimal("55.00"));
        assertThat(response.getCurrentStock()).isEqualTo(100);
        assertThat(response.getUpdatedAt()).isEqualTo("2026-01-19T14:30:00.000Z");
    }

    @Test
    void shouldCreatePriceUpdateResponseWithNoArgsConstructorAndSetters() {
        PriceUpdateResponse response = new PriceUpdateResponse();
        response.setProductId(2L);
        response.setProductCode("gadget-002");
        response.setProductName("Gadget");
        response.setVendorId(3L);
        response.setVendorName("Vendor Gamma");
        response.setOldPrice(new BigDecimal("70.00"));
        response.setNewPrice(new BigDecimal("75.50"));
        response.setCurrentStock(50);
        response.setUpdatedAt("2026-01-19T15:00:00.000Z");

        assertThat(response.getProductId()).isEqualTo(2L);
        assertThat(response.getProductCode()).isEqualTo("gadget-002");
        assertThat(response.getProductName()).isEqualTo("Gadget");
        assertThat(response.getVendorId()).isEqualTo(3L);
        assertThat(response.getVendorName()).isEqualTo("Vendor Gamma");
        assertThat(response.getOldPrice()).isEqualTo(new BigDecimal("70.00"));
        assertThat(response.getNewPrice()).isEqualTo(new BigDecimal("75.50"));
        assertThat(response.getCurrentStock()).isEqualTo(50);
        assertThat(response.getUpdatedAt()).isEqualTo("2026-01-19T15:00:00.000Z");
    }

    @Test
    void shouldHaveCorrectEqualsAndHashCode() {
        PriceUpdateResponse response1 = new PriceUpdateResponse(
            1L, "widget-001", "Widget", 2L, "Vendor Beta",
            new BigDecimal("50.00"), new BigDecimal("55.00"), 100, "2026-01-19T14:30:00.000Z"
        );
        PriceUpdateResponse response2 = new PriceUpdateResponse(
            1L, "widget-001", "Widget", 2L, "Vendor Beta",
            new BigDecimal("50.00"), new BigDecimal("55.00"), 100, "2026-01-19T14:30:00.000Z"
        );

        assertThat(response1).isEqualTo(response2);
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
    }

    @Test
    void shouldHaveCorrectToString() {
        PriceUpdateResponse response = new PriceUpdateResponse(
            1L, "widget-001", "Widget", 2L, "Vendor Beta",
            new BigDecimal("50.00"), new BigDecimal("55.00"), 100, "2026-01-19T14:30:00.000Z"
        );

        String toString = response.toString();
        assertThat(toString).contains("productId=1");
        assertThat(toString).contains("productCode=widget-001");
        assertThat(toString).contains("productName=Widget");
        assertThat(toString).contains("vendorId=2");
        assertThat(toString).contains("vendorName=Vendor Beta");
        assertThat(toString).contains("oldPrice=50.00");
        assertThat(toString).contains("newPrice=55.00");
        assertThat(toString).contains("currentStock=100");
        assertThat(toString).contains("updatedAt=2026-01-19T14:30:00.000Z");
    }
}
