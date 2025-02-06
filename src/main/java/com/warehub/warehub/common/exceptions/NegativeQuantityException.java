package com.warehub.warehub.common.exceptions;

public class NegativeQuantityException extends RuntimeException {
    public NegativeQuantityException(String message) {
        super(message);
    }
}
