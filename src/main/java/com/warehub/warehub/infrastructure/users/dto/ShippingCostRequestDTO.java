package com.warehub.warehub.infrastructure.users.dto;

import lombok.Data;

@Data
public class ShippingCostRequestDTO {
    private Long warehouseId;
    private Long userAddressId;
    private String courier;
    private float weight;
    private Float width;  // Optional (use wrapper class to allow null)
    private Float height; // Optional
    private Float length; // Optional
    private Boolean insurance; // Optional
    private Float itemValue;  // Optional
    private String price = "lowest"; // Default to "lowest"

    // Constructor with default values
    public ShippingCostRequestDTO() {
        this.price = "lowest";
    }
}
