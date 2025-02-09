package com.warehub.warehub.common.exceptions;

public class DuplicateWarehouseException extends RuntimeException {
    public DuplicateWarehouseException(String message) {
        super(message);
    }
}
