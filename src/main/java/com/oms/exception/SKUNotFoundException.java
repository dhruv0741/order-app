package com.oms.exception;

public class SKUNotFoundException extends RuntimeException {
    public SKUNotFoundException(String message) {
        super(message);
    }
    
    public SKUNotFoundException(Long skuId) {
        super("SKU not found with ID: " + skuId);
    }
}