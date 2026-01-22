package com.moyo.oms.service;

import com.moyo.oms.model.Product;
import com.moyo.oms.model.Vendor;
import com.moyo.oms.model.VendorProduct;
import com.moyo.oms.repository.VendorProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AllocationServiceTest {

    @Mock
    private VendorProductRepository vendorProductRepository;

    @Mock
    private VendorService vendorService;

    @InjectMocks
    private AllocationService allocationService;

    private Vendor vendorA;
    private Vendor vendorB;
    private Vendor vendorC;
    private Product product;

    @BeforeEach
    void setUp() {
        vendorA = new Vendor();
        vendorA.setId(1L);
        vendorA.setUsername("vendor-a");
        vendorA.setName("Vendor Alpha");

        vendorB = new Vendor();
        vendorB.setId(2L);
        vendorB.setUsername("vendor-b");
        vendorB.setName("Vendor Beta");

        vendorC = new Vendor();
        vendorC.setId(3L);
        vendorC.setUsername("vendor-c");
        vendorC.setName("Vendor Charlie");

        product = new Product();
        product.setId(1L);
        product.setProductCode("widget-001");
        product.setName("Widget");
    }

    @Test
    void allocate_selectsLowestPriceVendorWithSufficientStock() {
        // Given - Vendor B has lowest price ($45) with sufficient stock (50)
        VendorProduct vpA = createVendorProduct(vendorA, product, new BigDecimal("50.00"), 100);
        VendorProduct vpB = createVendorProduct(vendorB, product, new BigDecimal("45.00"), 50);

        // Already sorted by price ascending
        List<VendorProduct> candidates = Arrays.asList(vpB, vpA);
        when(vendorProductRepository.findEligibleVendorsForAllocation(1L, 0))
            .thenReturn(candidates);

        // When
        AllocationService.AllocationResult result = allocationService.allocate(1L, 10);

        // Then
        assertTrue(result.success());
        assertEquals(2L, result.vendorId());  // Vendor B (lowest price)
        verify(vendorService).decrementStock(2L, 1L, 10);
    }

    @Test
    void allocate_skipsVendorWithInsufficientStock() {
        // Given - Vendor B has lowest price but not enough stock (5), Vendor A has higher price but enough (100)
        VendorProduct vpA = createVendorProduct(vendorA, product, new BigDecimal("50.00"), 100);
        VendorProduct vpB = createVendorProduct(vendorB, product, new BigDecimal("45.00"), 5);

        // Sorted by price ascending
        List<VendorProduct> candidates = Arrays.asList(vpB, vpA);
        when(vendorProductRepository.findEligibleVendorsForAllocation(1L, 0))
            .thenReturn(candidates);

        // When - order for 10 units, B only has 5
        AllocationService.AllocationResult result = allocationService.allocate(1L, 10);

        // Then
        assertTrue(result.success());
        assertEquals(1L, result.vendorId());  // Vendor A (has enough stock)
        verify(vendorService).decrementStock(1L, 1L, 10);
    }

    @Test
    void allocate_returnsFailureWhenNoVendorHasSufficientStock() {
        // Given - All vendors have insufficient stock
        VendorProduct vpA = createVendorProduct(vendorA, product, new BigDecimal("50.00"), 50);
        VendorProduct vpB = createVendorProduct(vendorB, product, new BigDecimal("45.00"), 30);

        List<VendorProduct> candidates = Arrays.asList(vpB, vpA);
        when(vendorProductRepository.findEligibleVendorsForAllocation(1L, 0))
            .thenReturn(candidates);

        // When - order for 100 units, no one has enough
        AllocationService.AllocationResult result = allocationService.allocate(1L, 100);

        // Then
        assertFalse(result.success());
        assertNull(result.vendorId());
        verify(vendorService, never()).decrementStock(anyLong(), anyLong(), anyInt());
    }

    @Test
    void allocate_returnsFailureWhenNoCandidates() {
        // Given - No vendors with stock for this product
        when(vendorProductRepository.findEligibleVendorsForAllocation(1L, 0))
            .thenReturn(Collections.emptyList());

        // When
        AllocationService.AllocationResult result = allocationService.allocate(1L, 10);

        // Then
        assertFalse(result.success());
        assertNull(result.vendorId());
        verify(vendorService, never()).decrementStock(anyLong(), anyLong(), anyInt());
    }

    @Test
    void allocate_decrementsStockOfWinningVendor() {
        // Given
        VendorProduct vpB = createVendorProduct(vendorB, product, new BigDecimal("45.00"), 50);

        when(vendorProductRepository.findEligibleVendorsForAllocation(1L, 0))
            .thenReturn(Collections.singletonList(vpB));

        // When
        allocationService.allocate(1L, 25);

        // Then
        verify(vendorService).decrementStock(2L, 1L, 25);
    }

    @Test
    void allocate_withExactStockAmount_succeeds() {
        // Given - Vendor has exactly the quantity requested
        VendorProduct vpB = createVendorProduct(vendorB, product, new BigDecimal("45.00"), 10);

        when(vendorProductRepository.findEligibleVendorsForAllocation(1L, 0))
            .thenReturn(Collections.singletonList(vpB));

        // When - order for exactly 10 units
        AllocationService.AllocationResult result = allocationService.allocate(1L, 10);

        // Then
        assertTrue(result.success());
        assertEquals(2L, result.vendorId());
        verify(vendorService).decrementStock(2L, 1L, 10);
    }

    @Test
    void allocate_queriesRepositoryWithCorrectParameters() {
        // Given
        when(vendorProductRepository.findEligibleVendorsForAllocation(5L, 0))
            .thenReturn(Collections.emptyList());

        // When
        allocationService.allocate(5L, 10);

        // Then
        verify(vendorProductRepository).findEligibleVendorsForAllocation(5L, 0);
    }

    // ==================== FR12: Same-Price Deterministic Ordering Tests ====================

    @Test
    void allocate_withSamePriceVendors_selectsFirstVendorDeterministically() {
        // Given - Two vendors with same price ($45), both with sufficient stock
        // Repository returns sorted by price ASC, then vendor_id ASC (FR12 compliance)
        VendorProduct vpA = createVendorProduct(vendorA, product, new BigDecimal("45.00"), 50);
        VendorProduct vpB = createVendorProduct(vendorB, product, new BigDecimal("45.00"), 50);

        // Vendor A (ID=1) should come first due to deterministic ordering by vendor_id
        List<VendorProduct> candidates = Arrays.asList(vpA, vpB);
        when(vendorProductRepository.findEligibleVendorsForAllocation(1L, 0))
            .thenReturn(candidates);

        // When
        AllocationService.AllocationResult result = allocationService.allocate(1L, 10);

        // Then - Lower vendor ID wins deterministically (FR12)
        assertTrue(result.success());
        assertEquals(1L, result.vendorId());  // Vendor A wins (lower ID at same price)
        verify(vendorService).decrementStock(1L, 1L, 10);
    }

    @Test
    void allocate_withSamePriceVendors_skipsInsufficientStock() {
        // Given - Three vendors at $45:
        // - Vendor A: has insufficient stock (5)
        // - Vendor B: has sufficient stock (50)
        // - Vendor C: has sufficient stock (50)
        VendorProduct vpA = createVendorProduct(vendorA, product, new BigDecimal("45.00"), 5);
        VendorProduct vpB = createVendorProduct(vendorB, product, new BigDecimal("45.00"), 50);
        VendorProduct vpC = createVendorProduct(vendorC, product, new BigDecimal("45.00"), 50);

        // Sorted by price ASC, then vendor_id ASC
        List<VendorProduct> candidates = Arrays.asList(vpA, vpB, vpC);
        when(vendorProductRepository.findEligibleVendorsForAllocation(1L, 0))
            .thenReturn(candidates);

        // When - order for 10 units, A only has 5
        AllocationService.AllocationResult result = allocationService.allocate(1L, 10);

        // Then - Vendor B wins (first with sufficient stock at same price)
        assertTrue(result.success());
        assertEquals(2L, result.vendorId());  // Vendor B wins (first with enough stock)
        verify(vendorService).decrementStock(2L, 1L, 10);
    }

    @Test
    void allocate_withSamePriceVendors_multipleRoundsRemainDeterministic() {
        // Given - Two vendors with same price, verify determinism across multiple calls
        VendorProduct vpA = createVendorProduct(vendorA, product, new BigDecimal("45.00"), 100);
        VendorProduct vpB = createVendorProduct(vendorB, product, new BigDecimal("45.00"), 100);

        List<VendorProduct> candidates = Arrays.asList(vpA, vpB);
        when(vendorProductRepository.findEligibleVendorsForAllocation(1L, 0))
            .thenReturn(candidates);

        // When - Multiple allocation attempts
        AllocationService.AllocationResult result1 = allocationService.allocate(1L, 10);
        AllocationService.AllocationResult result2 = allocationService.allocate(1L, 10);
        AllocationService.AllocationResult result3 = allocationService.allocate(1L, 10);

        // Then - All allocations go to same vendor (deterministic)
        assertEquals(result1.vendorId(), result2.vendorId());
        assertEquals(result2.vendorId(), result3.vendorId());
        assertEquals(1L, result1.vendorId());  // Always Vendor A (lower ID)
    }

    @Test
    void allocate_withLargeQuantityExceedingAllStock_returnsFailure() {
        // Given - Order quantity exceeds all vendors' stock
        VendorProduct vpA = createVendorProduct(vendorA, product, new BigDecimal("50.00"), 100);
        VendorProduct vpB = createVendorProduct(vendorB, product, new BigDecimal("45.00"), 50);

        List<VendorProduct> candidates = Arrays.asList(vpB, vpA);
        when(vendorProductRepository.findEligibleVendorsForAllocation(1L, 0))
            .thenReturn(candidates);

        // When - order for 200 units, max available is 100
        AllocationService.AllocationResult result = allocationService.allocate(1L, 200);

        // Then - Allocation fails
        assertFalse(result.success());
        assertNull(result.vendorId());
        verify(vendorService, never()).decrementStock(anyLong(), anyLong(), anyInt());
    }

    private VendorProduct createVendorProduct(Vendor vendor, Product product, BigDecimal price, int stock) {
        VendorProduct vp = new VendorProduct();
        vp.setVendor(vendor);
        vp.setProduct(product);
        vp.setPrice(price);
        vp.setStock(stock);
        return vp;
    }
}
