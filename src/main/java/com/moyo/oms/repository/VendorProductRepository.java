package com.moyo.oms.repository;

import com.moyo.oms.model.VendorProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VendorProductRepository extends JpaRepository<VendorProduct, Long> {
    List<VendorProduct> findByVendorId(Long vendorId);

    @Query("SELECT vp FROM VendorProduct vp JOIN FETCH vp.product WHERE vp.vendor.id = :vendorId")
    List<VendorProduct> findByVendorIdWithProduct(@Param("vendorId") Long vendorId);

    Optional<VendorProduct> findByVendorIdAndProductId(Long vendorId, Long productId);

    @Query("SELECT vp FROM VendorProduct vp JOIN FETCH vp.product WHERE vp.vendor.id = :vendorId AND vp.product.id = :productId")
    Optional<VendorProduct> findByVendorIdAndProductIdWithProduct(@Param("vendorId") Long vendorId, @Param("productId") Long productId);

    List<VendorProduct> findByProductIdAndStockGreaterThanOrderByPriceAsc(Long productId, Integer minStock);

    /**
     * Find vendors eligible for allocation, sorted deterministically by price ASC, then vendor ID ASC.
     * This ensures consistent allocation when multiple vendors have the same price (FR12).
     */
    @Query("SELECT vp FROM VendorProduct vp " +
           "JOIN FETCH vp.vendor " +
           "JOIN FETCH vp.product " +
           "WHERE vp.product.id = :productId AND vp.stock > :minStock " +
           "ORDER BY vp.price ASC, vp.vendor.id ASC")
    List<VendorProduct> findEligibleVendorsForAllocation(@Param("productId") Long productId, @Param("minStock") Integer minStock);
}
