package com.warehub.warehub.common.exceptions;

public class DuplicateWarehouseInventoryException extends RuntimeException {
    public DuplicateWarehouseInventoryException(String message) {
        super(message);
    }
}
