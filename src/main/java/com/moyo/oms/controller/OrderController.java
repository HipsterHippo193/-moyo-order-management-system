package com.moyo.oms.controller;

import com.moyo.oms.dto.OrderRequest;
import com.moyo.oms.dto.OrderResponse;
import com.moyo.oms.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moyo.oms.security.SecurityUtils;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order management endpoints")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(
        summary = "Submit order",
        description = "Submit a new order for allocation to the best-priced vendor with stock"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Order created and allocated"),
        @ApiResponse(responseCode = "400", description = "Validation error or no stock available"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing JWT"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody OrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(
        summary = "Get vendor orders",
        description = "Get all orders allocated to the authenticated vendor, sorted by newest first"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing JWT")
    })
    public ResponseEntity<List<OrderResponse>> getVendorOrders() {
        Long vendorId = SecurityUtils.getCurrentVendorId();
        List<OrderResponse> orders = orderService.getVendorOrders(vendorId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    @Operation(
        summary = "Get order details",
        description = "Get details of a specific order allocated to the authenticated vendor"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order details retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing JWT"),
        @ApiResponse(responseCode = "404", description = "Order not found or not allocated to this vendor")
    })
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long orderId) {
        Long vendorId = SecurityUtils.getCurrentVendorId();
        OrderResponse order = orderService.getOrderById(orderId, vendorId);
        return ResponseEntity.ok(order);
    }
}
