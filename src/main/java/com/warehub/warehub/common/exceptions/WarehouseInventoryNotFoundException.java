package com.warehub.warehub.common.exceptions;

public class WarehouseInventoryNotFoundException extends RuntimeException {
    public WarehouseInventoryNotFoundException(String message) {
        super(message);
    }
}
