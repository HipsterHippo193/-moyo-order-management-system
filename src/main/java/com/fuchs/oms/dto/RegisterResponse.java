package com.fuchs.oms.dto;

import lombok.Data;

@Data
public class RegisterResponse {
    private Long vendorId;
    private String username;
    private String vendorName;
    private String message;
}
