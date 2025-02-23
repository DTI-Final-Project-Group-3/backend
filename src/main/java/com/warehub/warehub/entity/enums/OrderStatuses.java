package com.warehub.warehub.entity.enums;

import lombok.Getter;

@Getter
public enum OrderStatuses {
    WAITING_PAYMENT("Waiting payment") ,
    WAITING_PAYMENT_CONFIRMATION("Waiting payment confirmation"),
    PROCESSED("Processed"),
    SHIPPED("Shipped"),
    CONFIRMED("Confirmed"),
    CANCELED("Canceled");

    private final String status;

    OrderStatuses(String status) {
        this.status = status;
    }
}
