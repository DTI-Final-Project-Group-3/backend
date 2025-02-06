package com.warehub.warehub.common.exceptions;

public class DuplicateProductCategoryException extends RuntimeException {
    public DuplicateProductCategoryException(String message) {
        super(message);
    }
}
