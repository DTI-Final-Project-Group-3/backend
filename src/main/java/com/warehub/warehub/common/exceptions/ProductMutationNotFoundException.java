package com.warehub.warehub.common.exceptions;

public class ProductMutationNotFoundException extends RuntimeException {
    public ProductMutationNotFoundException(String message) {
        super(message);
    }
}
