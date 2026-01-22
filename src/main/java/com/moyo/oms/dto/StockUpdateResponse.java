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
    private String productName;
    private Long vendorId;
    private String vendorName;
    private Integer oldStock;
    private Integer newStock;
    private java.math.BigDecimal currentPrice;
    private String updatedAt;
}
