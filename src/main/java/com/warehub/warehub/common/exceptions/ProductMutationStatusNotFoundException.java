package com.warehub.warehub.common.exceptions;

public class ProductMutationStatusNotFoundException extends RuntimeException {
    public ProductMutationStatusNotFoundException(String message) {
        super(message);
    }
}
