package com.moyo.oms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceUpdateResponse {
    private Long productId;
    private String productCode;
    private String name;
    private BigDecimal newPrice;
    private String updatedAt;
}
