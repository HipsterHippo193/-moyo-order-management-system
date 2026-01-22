package com.moyo.oms.exception;

/**
 * Exception thrown when no vendor has sufficient stock to fulfill an order.
 * This is a business exception that results in a 400 Bad Request response.
 */
public class NoStockAvailableException extends RuntimeException {

    public NoStockAvailableException(String productName) {
        super("No vendor has stock for product: " + productName);
    }
}
