package com.warehub.warehub.entity.enums;

import com.warehub.warehub.common.exceptions.DataNotFoundException;
import lombok.Getter;

@Getter
public enum OrderStatuses {
    WAITING_PAYMENT(1, "Waiting payment") ,
    WAITING_PAYMENT_CONFIRMATION(2,"Waiting payment confirmation"),
    PROCESSED(3,"Processed"),
    SHIPPED(4, "Shipped"),
    CONFIRMED(5, "Confirmed"),
    CANCELED(6, "Canceled");

    private final Integer id;
    private final String status;

    OrderStatuses(Integer id, String status) {
        this.id = id;
        this.status = status;
    }

    public static OrderStatuses getOrderStatuses(Integer id) {
        for (OrderStatuses status : values()) {
            if (status.getId().equals(id)){
                return status;
            }
        }
        throw new DataNotFoundException("Order status with ID : "+id+" not found");
    }
}
