package com.moyo.oms.dto;

import lombok.Data;

@Data
public class ProductResponse {
    private Long id;
    private String productCode;
    private String name;
    private String description;
    private Long categoryId;
    private String categoryName;
    private String createdAt;
}
