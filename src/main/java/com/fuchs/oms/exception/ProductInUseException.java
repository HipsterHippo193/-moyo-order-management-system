package com.fuchs.oms.exception;

public class ProductInUseException extends RuntimeException {
    public ProductInUseException(String productName) {
        super("Cannot delete product '" + productName + "' because it has active orders");
    }
}
