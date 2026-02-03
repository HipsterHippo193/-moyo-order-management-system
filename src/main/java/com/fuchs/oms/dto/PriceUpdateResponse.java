package com.fuchs.oms.dto;

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
    private String productName;
    private Long vendorId;
    private String vendorName;
    private BigDecimal oldPrice;
    private BigDecimal newPrice;
    private Integer currentStock;
    private String updatedAt;
}
