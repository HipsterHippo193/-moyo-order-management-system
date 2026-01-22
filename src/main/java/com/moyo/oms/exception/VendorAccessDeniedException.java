package com.moyo.oms.exception;

public class VendorAccessDeniedException extends RuntimeException {
    public VendorAccessDeniedException(String message) {
        super(message);
    }
}
