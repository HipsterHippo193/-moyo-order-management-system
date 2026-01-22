package com.moyo.oms.service;

import com.moyo.oms.dto.PriceUpdateRequest;
import com.moyo.oms.dto.PriceUpdateResponse;
import com.moyo.oms.dto.StockUpdateRequest;
import com.moyo.oms.dto.StockUpdateResponse;
import com.moyo.oms.dto.VendorProductResponse;
import com.moyo.oms.exception.InsufficientStockException;
import com.moyo.oms.exception.ResourceNotFoundException;
import com.moyo.oms.model.VendorProduct;
import com.moyo.oms.repository.VendorProductRepository;
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
