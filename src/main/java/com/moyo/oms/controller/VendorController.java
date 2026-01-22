package com.moyo.oms.controller;

import com.moyo.oms.dto.PriceUpdateRequest;
import com.moyo.oms.dto.PriceUpdateResponse;
import com.moyo.oms.dto.StockUpdateRequest;
import com.moyo.oms.dto.StockUpdateResponse;
import com.moyo.oms.dto.VendorProductResponse;
import com.moyo.oms.exception.VendorAccessDeniedException;
import com.moyo.oms.security.SecurityUtils;
import com.moyo.oms.service.VendorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
}
