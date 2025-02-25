package com.warehub.warehub.common.exceptions;

public class PaymentProofAlreadyExistsException extends RuntimeException{
    public PaymentProofAlreadyExistsException(String msg) {
        super(msg);
    }

}
