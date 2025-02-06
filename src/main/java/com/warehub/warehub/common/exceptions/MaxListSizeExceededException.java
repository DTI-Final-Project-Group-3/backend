package com.warehub.warehub.common.exceptions;

public class MaxListSizeExceededException extends RuntimeException {
    public MaxListSizeExceededException(String message) {
        super(message);
    }
}
