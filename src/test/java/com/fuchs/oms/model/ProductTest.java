package com.fuchs.oms.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldPersistProductWithAllFields() {
        Product product = new Product();
        product.setProductCode("widget-001");
        product.setName("Widget");
        product.setDescription("Standard widget for demo purposes");

        Product saved = entityManager.persistFlushFind(product);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getProductCode()).isEqualTo("widget-001");
        assertThat(saved.getName()).isEqualTo("Widget");
        assertThat(saved.getDescription()).isEqualTo("Standard widget for demo purposes");
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldEnforceUniqueProductCode() {
        Product product1 = new Product();
        product1.setProductCode("unique-code");
        product1.setName("Product One");
        entityManager.persistAndFlush(product1);

        Product product2 = new Product();
        product2.setProductCode("unique-code");
        product2.setName("Product Two");

        org.junit.jupiter.api.Assertions.assertThrows(
            Exception.class,
            () -> entityManager.persistAndFlush(product2)
        );
    }

    @Test
    void shouldMapToProductsTable() {
        Product product = new Product();
        assertThat(product.getClass().getAnnotation(jakarta.persistence.Table.class).name())
            .isEqualTo("products");
    }
}
