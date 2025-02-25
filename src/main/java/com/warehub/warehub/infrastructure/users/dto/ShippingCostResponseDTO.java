package com.warehub.warehub.infrastructure.users.dto;

import lombok.Data;

import java.util.List;

import lombok.Data;
import java.util.List;

@Data
public class ShippingCostResponseDTO {
    private String courier;
    private List<ShippingCostDetail> costs;

    @Data
    public static class ShippingCostDetail {
        private String name;
        private String code;
        private String service;
        private String description;
        private float cost;
        private String etd;

        // Constructor
        public ShippingCostDetail(String service, float cost, String etd) {
            this.service = service;
            this.cost = cost;
            this.etd = etd;
        }
    }

    public ShippingCostResponseDTO(String courier, List<ShippingCostDetail> costs) {
        this.courier = courier;
        this.costs = costs;
    }
}
