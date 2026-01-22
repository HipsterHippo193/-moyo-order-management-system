package com.moyo.oms.repository;

import com.moyo.oms.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void shouldFindProductByProductCode() {
        Product product = new Product();
        product.setProductCode("widget-001");
        product.setName("Widget");
        product.setDescription("Test widget");
        entityManager.persistAndFlush(product);

        Optional<Product> found = productRepository.findByProductCode("widget-001");

        assertThat(found).isPresent();
        assertThat(found.get().getProductCode()).isEqualTo("widget-001");
        assertThat(found.get().getName()).isEqualTo("Widget");
    }

    @Test
    void shouldReturnEmptyWhenProductCodeNotFound() {
        Optional<Product> found = productRepository.findByProductCode("nonexistent");

        assertThat(found).isEmpty();
    }

    @Test
    void shouldSaveAndRetrieveProduct() {
        Product product = new Product();
        product.setProductCode("save-test");
        product.setName("Save Test Product");

        Product saved = productRepository.save(product);

        assertThat(saved.getId()).isNotNull();
        assertThat(productRepository.findById(saved.getId())).isPresent();
    }
}
