package com.moyo.oms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long orderId;
    private Long productId;
    private String productName;
    private Integer quantity;
    private Long allocatedVendorId;
    private String allocatedVendorName;
    private java.math.BigDecimal price;
    private java.math.BigDecimal totalPrice;
    private String status;
    private String createdAt;  // ISO 8601 format
}
