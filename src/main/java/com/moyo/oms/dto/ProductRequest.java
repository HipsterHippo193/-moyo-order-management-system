package com.moyo.oms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(max = 100, message = "Product name must be at most 100 characters")
    private String name;

    @NotBlank(message = "Product code is required")
    @Size(max = 50, message = "Product code must be at most 50 characters")
    private String productCode;

    @Size(max = 500, message = "Description must be at most 500 characters")
    private String description;

    private Long categoryId;
}
