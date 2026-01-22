package com.moyo.oms.repository;

import com.moyo.oms.model.Product;
import com.moyo.oms.model.Vendor;
import com.moyo.oms.model.VendorProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VendorProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VendorProductRepository vendorProductRepository;

    private Vendor vendorA;
    private Vendor vendorB;
    private Product widget;

    @BeforeEach
    void setUp() {
        vendorA = new Vendor();
        vendorA.setUsername("vendor-a");
        vendorA.setPassword("password");
        vendorA.setName("Vendor A");
        vendorA = entityManager.persistFlushFind(vendorA);

        vendorB = new Vendor();
        vendorB.setUsername("vendor-b");
        vendorB.setPassword("password");
        vendorB.setName("Vendor B");
        vendorB = entityManager.persistFlushFind(vendorB);

        widget = new Product();
        widget.setProductCode("widget-001");
        widget.setName("Widget");
        widget = entityManager.persistFlushFind(widget);
    }

    @Test
    void shouldFindByVendorId() {
        VendorProduct vp1 = new VendorProduct();
        vp1.setVendor(vendorA);
        vp1.setProduct(widget);
        vp1.setPrice(new BigDecimal("50.00"));
        vp1.setStock(100);
        entityManager.persistAndFlush(vp1);

        List<VendorProduct> found = vendorProductRepository.findByVendorId(vendorA.getId());

        assertThat(found).hasSize(1);
        assertThat(found.get(0).getVendor().getId()).isEqualTo(vendorA.getId());
    }

    @Test
    void shouldFindByVendorIdAndProductId() {
        VendorProduct vp = new VendorProduct();
        vp.setVendor(vendorA);
        vp.setProduct(widget);
        vp.setPrice(new BigDecimal("50.00"));
        vp.setStock(100);
        entityManager.persistAndFlush(vp);

        Optional<VendorProduct> found = vendorProductRepository.findByVendorIdAndProductId(
            vendorA.getId(), widget.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getPrice()).isEqualByComparingTo(new BigDecimal("50.00"));
    }

    @Test
    void shouldFindByProductIdWithStockOrderedByPrice() {
        // Vendor A: $50, 100 stock
        VendorProduct vpA = new VendorProduct();
        vpA.setVendor(vendorA);
        vpA.setProduct(widget);
        vpA.setPrice(new BigDecimal("50.00"));
        vpA.setStock(100);
        entityManager.persistAndFlush(vpA);

        // Vendor B: $45, 50 stock (cheaper)
        VendorProduct vpB = new VendorProduct();
        vpB.setVendor(vendorB);
        vpB.setProduct(widget);
        vpB.setPrice(new BigDecimal("45.00"));
        vpB.setStock(50);
        entityManager.persistAndFlush(vpB);

        List<VendorProduct> found = vendorProductRepository
            .findByProductIdAndStockGreaterThanOrderByPriceAsc(widget.getId(), 0);

        assertThat(found).hasSize(2);
        // Should be ordered by price ascending - vendor B first (cheaper)
        assertThat(found.get(0).getPrice()).isEqualByComparingTo(new BigDecimal("45.00"));
        assertThat(found.get(1).getPrice()).isEqualByComparingTo(new BigDecimal("50.00"));
    }

    @Test
    void shouldExcludeZeroStockFromStockQuery() {
        // Vendor A: $50, 100 stock
        VendorProduct vpA = new VendorProduct();
        vpA.setVendor(vendorA);
        vpA.setProduct(widget);
        vpA.setPrice(new BigDecimal("50.00"));
        vpA.setStock(100);
        entityManager.persistAndFlush(vpA);

        // Vendor B: $45, 0 stock (out of stock)
        VendorProduct vpB = new VendorProduct();
        vpB.setVendor(vendorB);
        vpB.setProduct(widget);
        vpB.setPrice(new BigDecimal("45.00"));
        vpB.setStock(0);
        entityManager.persistAndFlush(vpB);

        List<VendorProduct> found = vendorProductRepository
            .findByProductIdAndStockGreaterThanOrderByPriceAsc(widget.getId(), 0);

        // Only vendor A should be returned (has stock > 0)
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getVendor().getId()).isEqualTo(vendorA.getId());
    }
}
