package com.fuchs.oms.dto;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class VendorProductResponseTest {

    @Test
    void shouldCreateVendorProductResponseWithAllArgsConstructor() {
        VendorProductResponse response = new VendorProductResponse(
            1L, "widget-001", "Widget", new BigDecimal("50.00"), 100
        );

        assertThat(response.getProductId()).isEqualTo(1L);
        assertThat(response.getProductCode()).isEqualTo("widget-001");
        assertThat(response.getName()).isEqualTo("Widget");
        assertThat(response.getPrice()).isEqualTo(new BigDecimal("50.00"));
        assertThat(response.getStock()).isEqualTo(100);
    }

    @Test
    void shouldCreateVendorProductResponseWithNoArgsConstructorAndSetters() {
        VendorProductResponse response = new VendorProductResponse();
        response.setProductId(2L);
        response.setProductCode("gadget-002");
        response.setName("Gadget");
        response.setPrice(new BigDecimal("75.50"));
        response.setStock(50);

        assertThat(response.getProductId()).isEqualTo(2L);
        assertThat(response.getProductCode()).isEqualTo("gadget-002");
        assertThat(response.getName()).isEqualTo("Gadget");
        assertThat(response.getPrice()).isEqualTo(new BigDecimal("75.50"));
        assertThat(response.getStock()).isEqualTo(50);
    }

    @Test
    void shouldHaveCorrectEqualsAndHashCode() {
        VendorProductResponse response1 = new VendorProductResponse(
            1L, "widget-001", "Widget", new BigDecimal("50.00"), 100
        );
        VendorProductResponse response2 = new VendorProductResponse(
            1L, "widget-001", "Widget", new BigDecimal("50.00"), 100
        );

        assertThat(response1).isEqualTo(response2);
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
    }

    @Test
    void shouldHaveCorrectToString() {
        VendorProductResponse response = new VendorProductResponse(
            1L, "widget-001", "Widget", new BigDecimal("50.00"), 100
        );

        String toString = response.toString();
        assertThat(toString).contains("productId=1");
        assertThat(toString).contains("productCode=widget-001");
        assertThat(toString).contains("name=Widget");
        assertThat(toString).contains("price=50.00");
        assertThat(toString).contains("stock=100");
    }
}
