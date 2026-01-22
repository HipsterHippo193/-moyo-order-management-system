package com.moyo.oms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockUpdateResponse {
    private Long productId;
    private String productCode;
    private String name;
    private Integer newStock;
    private String updatedAt;
}
