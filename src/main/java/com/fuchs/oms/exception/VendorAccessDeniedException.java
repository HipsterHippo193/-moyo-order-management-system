package com.fuchs.oms.exception;

public class VendorAccessDeniedException extends RuntimeException {
    public VendorAccessDeniedException(String message) {
        super(message);
    }
}
