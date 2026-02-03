package com.fuchs.oms.service;

import com.fuchs.oms.exception.InsufficientStockException;
import com.fuchs.oms.exception.ResourceNotFoundException;
import com.fuchs.oms.model.VendorProduct;
import com.fuchs.oms.repository.VendorProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class VendorServiceIntegrationTest {

    @Autowired
    private VendorService vendorService;

    @Autowired
    private VendorProductRepository vendorProductRepository;

    @Test
    void decrementStock_persistsToDatabase() {
        // Given - Vendor 1 has 100 stock for product 1

        // When
        vendorService.decrementStock(1L, 1L, 25);

        // Then
        VendorProduct updated = vendorProductRepository
            .findByVendorIdAndProductIdWithProduct(1L, 1L)
            .orElseThrow();
        assertThat(updated.getStock()).isEqualTo(75);
    }

    @Test
    void decrementStock_verifiedBySubsequentRead() {
        // Given - Vendor 1 has 100 stock for product 1
        int initialStock = vendorProductRepository
            .findByVendorIdAndProductIdWithProduct(1L, 1L)
            .orElseThrow()
            .getStock();
        assertThat(initialStock).isEqualTo(100);

        // When
        vendorService.decrementStock(1L, 1L, 30);

        // Then - Subsequent read confirms the reduction
        int newStock = vendorProductRepository
            .findByVendorIdAndProductIdWithProduct(1L, 1L)
            .orElseThrow()
            .getStock();
        assertThat(newStock).isEqualTo(70);
    }

    @Test
    void decrementStock_multipleDecrementsInSequence() {
        // Given - Vendor 1 has 100 stock for product 1

        // When - Multiple decrements
        vendorService.decrementStock(1L, 1L, 20);
        vendorService.decrementStock(1L, 1L, 30);
        vendorService.decrementStock(1L, 1L, 10);

        // Then
        VendorProduct updated = vendorProductRepository
            .findByVendorIdAndProductIdWithProduct(1L, 1L)
            .orElseThrow();
        assertThat(updated.getStock()).isEqualTo(40); // 100 - 20 - 30 - 10 = 40
    }

    @Test
    void decrementStock_exceptionLeavesDataUnchanged() {
        // Given - Vendor 2 has 50 stock
        int initialStock = vendorProductRepository
            .findByVendorIdAndProductIdWithProduct(2L, 1L)
            .orElseThrow()
            .getStock();
        assertThat(initialStock).isEqualTo(50);

        // When - Attempt to decrement more than available
        assertThatThrownBy(() -> vendorService.decrementStock(2L, 1L, 100))
            .isInstanceOf(InsufficientStockException.class);

        // Then - Stock unchanged
        int finalStock = vendorProductRepository
            .findByVendorIdAndProductIdWithProduct(2L, 1L)
            .orElseThrow()
            .getStock();
        assertThat(finalStock).isEqualTo(initialStock);
    }

    @Test
    void decrementStock_decrementToExactlyZero() {
        // Given - Vendor 2 has 50 stock
        int initialStock = vendorProductRepository
            .findByVendorIdAndProductIdWithProduct(2L, 1L)
            .orElseThrow()
            .getStock();
        assertThat(initialStock).isEqualTo(50);

        // When - Decrement exactly the available amount
        vendorService.decrementStock(2L, 1L, 50);

        // Then - Stock is exactly 0
        VendorProduct updated = vendorProductRepository
            .findByVendorIdAndProductIdWithProduct(2L, 1L)
            .orElseThrow();
        assertThat(updated.getStock()).isEqualTo(0);
    }

    @Test
    void decrementStock_nonExistentVendorProduct_throwsResourceNotFoundException() {
        // When/Then
        assertThatThrownBy(() -> vendorService.decrementStock(1L, 999L, 10))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Product not found for vendor");
    }

    @Test
    void decrementStock_vendorWithZeroStock_throwsInsufficientStockException() {
        // Given - Vendor 3 has 0 stock

        // When/Then
        assertThatThrownBy(() -> vendorService.decrementStock(3L, 1L, 1))
            .isInstanceOf(InsufficientStockException.class)
            .hasMessageContaining("Insufficient stock")
            .hasMessageContaining("available=0");
    }
}
