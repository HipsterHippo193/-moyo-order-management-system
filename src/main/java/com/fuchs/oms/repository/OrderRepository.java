package com.fuchs.oms.repository;

import com.fuchs.oms.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Find all orders allocated to a specific vendor, ordered by creation date descending.
     * Used in Epic 4B for vendor order visibility.
     *
     * @param vendorId the vendor's ID
     * @return list of orders for this vendor, newest first
     */
    List<Order> findByAllocatedVendorIdOrderByCreatedAtDesc(Long vendorId);

    /**
     * Check if there are any active orders for a product.
     * Used to prevent deletion of products with active orders.
     *
     * @param productId the product's ID
     * @return true if there are orders for this product
     */
    boolean existsByProductId(Long productId);
}
