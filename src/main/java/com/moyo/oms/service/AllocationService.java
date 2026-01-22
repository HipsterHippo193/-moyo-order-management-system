package com.moyo.oms.service;

import com.moyo.oms.model.VendorProduct;
import com.moyo.oms.repository.VendorProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AllocationService {

    private final VendorProductRepository vendorProductRepository;
    private final VendorService vendorService;

    /**
     * Allocates an order to the vendor with lowest price and sufficient stock.
     * Uses deterministic ordering (price ASC, vendor_id ASC) for consistent allocation
     * when multiple vendors have the same price (FR12).
     *
     * @param productId the product to allocate
     * @param quantity the quantity required
     * @return AllocationResult with vendorId if successful, null if no vendor has stock
     */
    @Transactional
    public AllocationResult allocate(Long productId, int quantity) {
        // Find vendors with stock > 0, sorted by price ASC then vendor_id ASC (FR12 compliance)
        List<VendorProduct> candidates = vendorProductRepository
            .findEligibleVendorsForAllocation(productId, 0);

        // Find first vendor with sufficient stock
        Optional<VendorProduct> winner = candidates.stream()
            .filter(vp -> vp.getStock() >= quantity)
            .findFirst();

        if (winner.isEmpty()) {
            return new AllocationResult(null, false);
        }

        VendorProduct allocated = winner.get();

        // Decrement stock
        vendorService.decrementStock(
            allocated.getVendor().getId(),
            productId,
            quantity
        );

        return new AllocationResult(allocated.getVendor().getId(), true);
    }

    /**
     * Result of allocation attempt.
     *
     * @param vendorId the ID of the allocated vendor, or null if allocation failed
     * @param success true if allocation succeeded, false otherwise
     */
    public record AllocationResult(Long vendorId, boolean success) {}
}
