package com.moyo.oms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendorProductResponse {
    private Long productId;
    private String productCode;
    private String name;
    private BigDecimal price;
    private Integer stock;
}
