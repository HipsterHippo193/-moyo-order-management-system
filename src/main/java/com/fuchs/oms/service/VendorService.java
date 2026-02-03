package com.fuchs.oms.service;

import com.fuchs.oms.dto.EnrollProductRequest;
import com.fuchs.oms.dto.PriceUpdateRequest;
import com.fuchs.oms.dto.PriceUpdateResponse;
import com.fuchs.oms.dto.StockUpdateRequest;
import com.fuchs.oms.dto.StockUpdateResponse;
import com.fuchs.oms.dto.VendorProductResponse;
import com.fuchs.oms.exception.AlreadyEnrolledException;
import com.fuchs.oms.exception.InsufficientStockException;
import com.fuchs.oms.exception.ProductNotFoundException;
import com.fuchs.oms.exception.ResourceNotFoundException;
import com.fuchs.oms.exception.VendorNotFoundException;
import com.fuchs.oms.model.Product;
import com.fuchs.oms.model.Vendor;
import com.fuchs.oms.model.VendorProduct;
import com.fuchs.oms.repository.ProductRepository;
import com.fuchs.oms.repository.VendorProductRepository;
import com.fuchs.oms.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VendorService {

    private final VendorProductRepository vendorProductRepository;
    private final VendorRepository vendorRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<VendorProductResponse> getVendorProducts(Long vendorId) {
        List<VendorProduct> vendorProducts = vendorProductRepository.findByVendorIdWithProduct(vendorId);
        return vendorProducts.stream()
            .map(this::toVendorProductResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public PriceUpdateResponse updatePrice(Long vendorId, Long productId, PriceUpdateRequest request) {
        VendorProduct vendorProduct = vendorProductRepository
            .findByVendorIdAndProductIdWithProduct(vendorId, productId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Product not found for vendor: vendorId=" + vendorId + ", productId=" + productId));

        java.math.BigDecimal oldPrice = vendorProduct.getPrice();
        vendorProduct.setPrice(request.getPrice());
        VendorProduct saved = vendorProductRepository.save(vendorProduct);

        return new PriceUpdateResponse(
            saved.getProduct().getId(),
            saved.getProduct().getProductCode(),
            saved.getProduct().getName(),
            saved.getVendor().getId(),
            saved.getVendor().getName(),
            oldPrice,
            saved.getPrice(),
            saved.getStock(),
            Instant.now().toString()
        );
    }

    @Transactional
    public StockUpdateResponse updateStock(Long vendorId, Long productId, StockUpdateRequest request) {
        VendorProduct vendorProduct = vendorProductRepository
            .findByVendorIdAndProductIdWithProduct(vendorId, productId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Product not found for vendor: vendorId=" + vendorId + ", productId=" + productId));

        Integer oldStock = vendorProduct.getStock();
        vendorProduct.setStock(request.getStock());
        VendorProduct saved = vendorProductRepository.save(vendorProduct);

        return new StockUpdateResponse(
            saved.getProduct().getId(),
            saved.getProduct().getProductCode(),
            saved.getProduct().getName(),
            saved.getVendor().getId(),
            saved.getVendor().getName(),
            oldStock,
            saved.getStock(),
            saved.getPrice(),
            Instant.now().toString()
        );
    }

    @Transactional
    public void decrementStock(Long vendorId, Long productId, int quantity) {
        VendorProduct vendorProduct = vendorProductRepository
            .findByVendorIdAndProductIdWithProduct(vendorId, productId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Product not found for vendor: vendorId=" + vendorId + ", productId=" + productId));

        int currentStock = vendorProduct.getStock();
        if (currentStock < quantity) {
            throw new InsufficientStockException(
                "Insufficient stock: available=" + currentStock + ", requested=" + quantity);
        }

        vendorProduct.setStock(currentStock - quantity);
        vendorProductRepository.save(vendorProduct);
    }

    @Transactional
    public VendorProductResponse enrollProduct(Long vendorId, EnrollProductRequest request) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new VendorNotFoundException(vendorId));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + request.getProductId()));

        // Check if already enrolled
        if (vendorProductRepository.findByVendorIdAndProductId(vendorId, request.getProductId()).isPresent()) {
            throw new AlreadyEnrolledException(vendor.getName(), product.getName());
        }

        VendorProduct vendorProduct = new VendorProduct();
        vendorProduct.setVendor(vendor);
        vendorProduct.setProduct(product);
        vendorProduct.setPrice(request.getPrice());
        vendorProduct.setStock(request.getStock());

        VendorProduct saved = vendorProductRepository.save(vendorProduct);

        return toVendorProductResponse(saved);
    }

    @Transactional
    public void unenrollProduct(Long vendorId, Long productId) {
        VendorProduct vendorProduct = vendorProductRepository
                .findByVendorIdAndProductId(vendorId, productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vendor is not enrolled in this product: vendorId=" + vendorId + ", productId=" + productId));

        vendorProductRepository.delete(vendorProduct);
    }

    private VendorProductResponse toVendorProductResponse(VendorProduct vp) {
        return new VendorProductResponse(
            vp.getProduct().getId(),
            vp.getProduct().getProductCode(),
            vp.getProduct().getName(),
            vp.getPrice(),
            vp.getStock()
        );
    }
}
