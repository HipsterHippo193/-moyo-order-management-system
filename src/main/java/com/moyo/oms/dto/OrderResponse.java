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
    private Integer quantity;
    private Long allocatedTo;  // null if no allocation
    private String status;
    private String createdAt;  // ISO 8601 format
}
