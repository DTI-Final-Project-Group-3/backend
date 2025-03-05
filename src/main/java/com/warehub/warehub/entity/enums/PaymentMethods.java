package com.warehub.warehub.entity.enums;

import com.warehub.warehub.common.exceptions.DataNotFoundException;
import lombok.Getter;

@Getter
public enum PaymentMethods {
    PAYMENT_GATEWAY_METHOD(1L, "Payment gateway"),
    PAYMENT_MANUAL_METHOD(2L, "Payment manual");

    private final Long id;
    private final String name;

    PaymentMethods(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static PaymentMethods getId(Long id) {
        for (PaymentMethods methods : values()) {
            if (methods.id.equals(id)) {
                return methods;
            }
        }
        throw new DataNotFoundException("Payment method with id " + id + " not found");
    }
}
