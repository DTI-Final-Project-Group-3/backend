package com.warehub.warehub.common.exceptions;

public class ProductMutationTypeNotFoundException extends RuntimeException {
    public ProductMutationTypeNotFoundException(String message) {
        super(message);
    }
}
