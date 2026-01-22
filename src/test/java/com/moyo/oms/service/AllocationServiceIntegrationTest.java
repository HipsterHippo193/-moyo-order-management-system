package com.moyo.oms.service;

import com.moyo.oms.model.VendorProduct;
import com.moyo.oms.repository.VendorProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for AllocationService verifying end-to-end allocation behavior
 * with actual database and seed data.
 *
 * FR10: System allocates order to vendor with lowest price who has sufficient stock
 * FR11: System skips vendors with zero stock during allocation
 * FR12: System handles allocation when multiple vendors have same price (first match wins)
 */
@SpringBootTest
@Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AllocationServiceIntegrationTest {

    @Autowired
    private AllocationService allocationService;

    @Autowired
    private VendorProductRepository vendorProductRepository;

    // ==================== FR10: Lowest Price With Stock Tests ====================

    @Test
    void allocate_withSeedData_allocatesToVendorBeta() {
        // Given - Seed data:
        // Vendor A: $50, 100 stock
        // Vendor B: $45, 50 stock (lowest price with stock)
        // Vendor C: $40, 0 stock (skipped)

        // When
        AllocationService.AllocationResult result = allocationService.allocate(1L, 10);

        // Then - Vendor Beta (ID=2) wins at $45 with 50 stock
        assertThat(result.success()).isTrue();
        assertThat(result.vendorId()).isEqualTo(2L);
    }

    @Test
    void allocate_decrementsWinningVendorStock() {
        // Given - Vendor B has 50 stock
        int initialStock = vendorProductRepository
            .findByVendorIdAndProductIdWithProduct(2L, 1L)
            .orElseThrow()
            .getStock();
        assertThat(initialStock).isEqualTo(50);

        // When
        allocationService.allocate(1L, 10);

        // Then - Stock decremented by 10
        int newStock = vendorProductRepository
            .findByVendorIdAndProductIdWithProduct(2L, 1L)
            .orElseThrow()
            .getStock();
        assertThat(newStock).isEqualTo(40);
    }

    @Test
    void allocate_largeQuantity_selectsVendorWithEnoughStock() {
        // Given - Order for 60 units
        // Vendor B only has 50, so should fall back to Vendor A (100 stock)

        // When
        AllocationService.AllocationResult result = allocationService.allocate(1L, 60);

        // Then - Vendor Alpha (ID=1) wins (has 100 stock)
        assertThat(result.success()).isTrue();
        assertThat(result.vendorId()).isEqualTo(1L);
    }

    @Test
    void allocate_veryLargeQuantity_returnsFailure() {
        // Given - Order for 200 units (exceeds all vendors' stock)

        // When
        AllocationService.AllocationResult result = allocationService.allocate(1L, 200);

        // Then - Allocation fails
        assertThat(result.success()).isFalse();
        assertThat(result.vendorId()).isNull();
    }

    // ==================== FR11: Skip Zero Stock Vendors Tests ====================

    @Test
    void allocate_skipsVendorWithZeroStock() {
        // Given - Vendor C has lowest price ($40) but 0 stock
        // Verify C is not selected even though cheapest

        // When
        AllocationService.AllocationResult result = allocationService.allocate(1L, 10);

        // Then - NOT Vendor C (ID=3)
        assertThat(result.success()).isTrue();
        assertThat(result.vendorId()).isNotEqualTo(3L);
        assertThat(result.vendorId()).isEqualTo(2L); // Vendor B wins
    }

    @Test
    void allocate_afterDrainingStock_skipsVendor() {
        // Given - Drain Vendor B's stock completely
        allocationService.allocate(1L, 50); // Takes all 50 from B

        // When - Second allocation
        AllocationService.AllocationResult result = allocationService.allocate(1L, 10);

        // Then - Falls back to Vendor A (B now has 0 stock)
        assertThat(result.success()).isTrue();
        assertThat(result.vendorId()).isEqualTo(1L);
    }

    // ==================== FR12: Same Price Deterministic Tests ====================

    @Test
    void allocate_samePriceVendors_selectsLowerVendorIdDeterministically() {
        // Given - Set both vendors to same price ($45)
        // Update Vendor A price from $50 to $45 to create same-price scenario
        VendorProduct vpA = vendorProductRepository.findByVendorIdAndProductIdWithProduct(1L, 1L).orElseThrow();
        vpA.setPrice(new BigDecimal("45.00"));
        vendorProductRepository.save(vpA);

        // When
        AllocationService.AllocationResult result = allocationService.allocate(1L, 10);

        // Then - Lower vendor ID wins when prices are equal (FR12)
        // With same price, vendor_id ordering determines winner
        assertThat(result.success()).isTrue();
        assertThat(result.vendorId()).isEqualTo(1L);  // Vendor A (lower ID) wins
    }

    @Test
    void allocate_samePriceVendors_multipleAllocationsDeterministic() {
        // Given - Set both vendors to same price ($45)
        VendorProduct vpA = vendorProductRepository.findByVendorIdAndProductIdWithProduct(1L, 1L).orElseThrow();
        vpA.setPrice(new BigDecimal("45.00"));
        vendorProductRepository.save(vpA);

        // When - Multiple allocation attempts
        AllocationService.AllocationResult result1 = allocationService.allocate(1L, 10);
        AllocationService.AllocationResult result2 = allocationService.allocate(1L, 10);
        AllocationService.AllocationResult result3 = allocationService.allocate(1L, 10);

        // Then - All go to same vendor (deterministic - FR12)
        assertThat(result1.vendorId()).isEqualTo(result2.vendorId());
        assertThat(result2.vendorId()).isEqualTo(result3.vendorId());
        assertThat(result1.vendorId()).isEqualTo(1L);  // Always Vendor A
    }

    // ==================== Query Verification Tests ====================

    @Test
    void findEligibleVendors_returnsSortedByPriceThenVendorId() {
        // Given - Seed data with varying prices

        // When
        List<VendorProduct> candidates = vendorProductRepository
            .findEligibleVendorsForAllocation(1L, 0);

        // Then - Sorted by price ASC (excluding zero stock)
        assertThat(candidates).isNotEmpty();
        // Vendor B ($45) should come before Vendor A ($50)
        // Vendor C ($40) excluded because stock = 0
        assertThat(candidates.get(0).getVendor().getId()).isEqualTo(2L);
        assertThat(candidates.get(0).getPrice()).isEqualTo(new BigDecimal("45.00"));
    }

    @Test
    void findEligibleVendors_excludesZeroStockVendors() {
        // Given - Seed data has Vendor C with 0 stock

        // When
        List<VendorProduct> candidates = vendorProductRepository
            .findEligibleVendorsForAllocation(1L, 0);

        // Then - Vendor C (ID=3) should not be in results
        boolean hasVendorC = candidates.stream()
            .anyMatch(vp -> vp.getVendor().getId().equals(3L));
        assertThat(hasVendorC).isFalse();
    }

    // ==================== End-to-End Allocation Flow Tests ====================

    @Test
    void allocate_multipleOrders_decrementsStockCorrectly() {
        // Given - Vendor B starts with 50 stock

        // When - Multiple orders totaling 40 units
        allocationService.allocate(1L, 15);
        allocationService.allocate(1L, 15);
        allocationService.allocate(1L, 10);

        // Then - Stock reduced to 10
        VendorProduct vp = vendorProductRepository
            .findByVendorIdAndProductIdWithProduct(2L, 1L)
            .orElseThrow();
        assertThat(vp.getStock()).isEqualTo(10);
    }

    @Test
    void allocate_exhaustVendorBStock_fallsBackToVendorA() {
        // Given - Allocate all of Vendor B's stock
        allocationService.allocate(1L, 50); // Takes all 50 from B

        // When - Next order
        AllocationService.AllocationResult result = allocationService.allocate(1L, 20);

        // Then - Falls back to Vendor A
        assertThat(result.success()).isTrue();
        assertThat(result.vendorId()).isEqualTo(1L);

        // Verify A's stock decremented
        VendorProduct vpA = vendorProductRepository
            .findByVendorIdAndProductIdWithProduct(1L, 1L)
            .orElseThrow();
        assertThat(vpA.getStock()).isEqualTo(80);
    }
}
