package com.fuchs.oms.controller;

import com.fuchs.oms.dto.EnrollProductRequest;
import com.fuchs.oms.dto.PriceUpdateRequest;
import com.fuchs.oms.dto.PriceUpdateResponse;
import com.fuchs.oms.dto.StockUpdateRequest;
import com.fuchs.oms.dto.StockUpdateResponse;
import com.fuchs.oms.dto.VendorProductResponse;
import com.fuchs.oms.exception.VendorAccessDeniedException;
import com.fuchs.oms.security.SecurityUtils;
import com.fuchs.oms.service.VendorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendors")
@RequiredArgsConstructor
@Tag(name = "Vendors", description = "Vendor product management endpoints")
public class VendorController {

    private final VendorService vendorService;

    @GetMapping("/{vendorId}/products")
    @Operation(
        summary = "Get vendor products",
        description = "Get all products with prices for the authenticated vendor"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing JWT"),
        @ApiResponse(responseCode = "403", description = "Forbidden - cannot access other vendor's data")
    })
    public ResponseEntity<List<VendorProductResponse>> getProducts(@PathVariable Long vendorId) {
        Long currentVendorId = SecurityUtils.getCurrentVendorId();
        if (!currentVendorId.equals(vendorId)) {
            throw new VendorAccessDeniedException("Access denied: You can only access your own data");
        }
        List<VendorProductResponse> products = vendorService.getVendorProducts(vendorId);
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{vendorId}/products/{productId}/price")
    @Operation(
        summary = "Update product price",
        description = "Update the price for a specific product for the authenticated vendor"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Price updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid price value"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing JWT"),
        @ApiResponse(responseCode = "403", description = "Forbidden - cannot update other vendor's prices"),
        @ApiResponse(responseCode = "404", description = "Product not found for this vendor")
    })
    public ResponseEntity<PriceUpdateResponse> updatePrice(
            @PathVariable Long vendorId,
            @PathVariable Long productId,
            @Valid @RequestBody PriceUpdateRequest request) {
        Long currentVendorId = SecurityUtils.getCurrentVendorId();
        if (!currentVendorId.equals(vendorId)) {
            throw new VendorAccessDeniedException("Access denied: You can only update your own prices");
        }
        PriceUpdateResponse response = vendorService.updatePrice(vendorId, productId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{vendorId}/products/{productId}/stock")
    @Operation(
        summary = "Update product stock",
        description = "Update the stock level for a specific product for the authenticated vendor"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid stock value"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing JWT"),
        @ApiResponse(responseCode = "403", description = "Forbidden - cannot update other vendor's stock"),
        @ApiResponse(responseCode = "404", description = "Product not found for this vendor")
    })
    public ResponseEntity<StockUpdateResponse> updateStock(
            @PathVariable Long vendorId,
            @PathVariable Long productId,
            @Valid @RequestBody StockUpdateRequest request) {
        Long currentVendorId = SecurityUtils.getCurrentVendorId();
        if (!currentVendorId.equals(vendorId)) {
            throw new VendorAccessDeniedException("Access denied: You can only update your own stock");
        }
        StockUpdateResponse response = vendorService.updateStock(vendorId, productId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{vendorId}/products")
    @Operation(
        summary = "Enroll in a product",
        description = "Vendor enrolls in a product to start supplying it with a set price and stock"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successfully enrolled in product"),
        @ApiResponse(responseCode = "400", description = "Validation error"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing JWT"),
        @ApiResponse(responseCode = "403", description = "Forbidden - cannot enroll for another vendor"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "409", description = "Already enrolled in this product")
    })
    public ResponseEntity<VendorProductResponse> enrollProduct(
            @PathVariable Long vendorId,
            @Valid @RequestBody EnrollProductRequest request) {
        Long currentVendorId = SecurityUtils.getCurrentVendorId();
        if (!currentVendorId.equals(vendorId)) {
            throw new VendorAccessDeniedException("Access denied: You can only enroll yourself in products");
        }
        VendorProductResponse response = vendorService.enrollProduct(vendorId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{vendorId}/products/{productId}")
    @Operation(
        summary = "Unenroll from a product",
        description = "Vendor stops supplying a product"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Successfully unenrolled from product"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing JWT"),
        @ApiResponse(responseCode = "403", description = "Forbidden - cannot unenroll another vendor"),
        @ApiResponse(responseCode = "404", description = "Vendor is not enrolled in this product")
    })
    public ResponseEntity<Void> unenrollProduct(
            @PathVariable Long vendorId,
            @PathVariable Long productId) {
        Long currentVendorId = SecurityUtils.getCurrentVendorId();
        if (!currentVendorId.equals(vendorId)) {
            throw new VendorAccessDeniedException("Access denied: You can only unenroll yourself from products");
        }
        vendorService.unenrollProduct(vendorId, productId);
        return ResponseEntity.noContent().build();
    }
}
