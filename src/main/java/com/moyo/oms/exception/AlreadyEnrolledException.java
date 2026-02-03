package com.moyo.oms.exception;

public class AlreadyEnrolledException extends RuntimeException {
    public AlreadyEnrolledException(String vendorName, String productName) {
        super("Vendor '" + vendorName + "' is already enrolled in product '" + productName + "'");
    }
}
