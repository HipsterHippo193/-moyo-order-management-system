package com.fuchs.oms.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VendorProductTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldPersistVendorProductWithAllFields() {
        Vendor vendor = new Vendor();
        vendor.setUsername("test-vendor");
        vendor.setPassword("password");
        vendor.setName("Test Vendor");
        vendor = entityManager.persistFlushFind(vendor);

        Product product = new Product();
        product.setProductCode("test-product");
        product.setName("Test Product");
        product = entityManager.persistFlushFind(product);

        VendorProduct vendorProduct = new VendorProduct();
        vendorProduct.setVendor(vendor);
        vendorProduct.setProduct(product);
        vendorProduct.setPrice(new BigDecimal("49.99"));
        vendorProduct.setStock(100);

        VendorProduct saved = entityManager.persistFlushFind(vendorProduct);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getVendor().getId()).isEqualTo(vendor.getId());
        assertThat(saved.getProduct().getId()).isEqualTo(product.getId());
        assertThat(saved.getPrice()).isEqualByComparingTo(new BigDecimal("49.99"));
        assertThat(saved.getStock()).isEqualTo(100);
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldEnforceUniqueVendorProductCombination() {
        Vendor vendor = new Vendor();
        vendor.setUsername("vendor-unique");
        vendor.setPassword("password");
        vendor.setName("Unique Vendor");
        vendor = entityManager.persistFlushFind(vendor);

        Product product = new Product();
        product.setProductCode("product-unique");
        product.setName("Unique Product");
        product = entityManager.persistFlushFind(product);

        VendorProduct vp1 = new VendorProduct();
        vp1.setVendor(vendor);
        vp1.setProduct(product);
        vp1.setPrice(new BigDecimal("10.00"));
        vp1.setStock(50);
        entityManager.persistAndFlush(vp1);

        VendorProduct vp2 = new VendorProduct();
        vp2.setVendor(vendor);
        vp2.setProduct(product);
        vp2.setPrice(new BigDecimal("20.00"));
        vp2.setStock(100);

        org.junit.jupiter.api.Assertions.assertThrows(
            Exception.class,
            () -> entityManager.persistAndFlush(vp2)
        );
    }

    @Test
    void shouldMapToVendorProductsTable() {
        VendorProduct vendorProduct = new VendorProduct();
        assertThat(vendorProduct.getClass().getAnnotation(jakarta.persistence.Table.class).name())
            .isEqualTo("vendor_products");
    }

    @Test
    void shouldHaveManyToOneRelationships() throws NoSuchFieldException {
        VendorProduct vendorProduct = new VendorProduct();

        assertThat(vendorProduct.getClass().getDeclaredField("vendor")
            .isAnnotationPresent(jakarta.persistence.ManyToOne.class)).isTrue();
        assertThat(vendorProduct.getClass().getDeclaredField("product")
            .isAnnotationPresent(jakarta.persistence.ManyToOne.class)).isTrue();
    }
}
