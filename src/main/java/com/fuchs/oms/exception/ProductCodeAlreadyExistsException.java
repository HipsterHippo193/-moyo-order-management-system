package com.fuchs.oms.exception;

public class ProductCodeAlreadyExistsException extends RuntimeException {
    public ProductCodeAlreadyExistsException(String productCode) {
        super("Product code already exists: " + productCode);
    }
}
