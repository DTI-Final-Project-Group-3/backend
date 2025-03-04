package com.warehub.warehub.infrastructure.users.dto;

import lombok.Data;

import java.util.List;

@Data
public class ShippingCostResponseDTO {
    private String couriers;
    private List<ShippingCostDetail> details;

    @Data
    public static class ShippingCostDetail {
        private String name;
        private String code;
        private String service;
        private String description;
        private float cost;
        private String etd;

        public ShippingCostDetail() {
        }

        public ShippingCostDetail(String service, String name, String code, String description, float cost, String etd) {
            this.name = name;
            this.code = code;
            this.description = description;
            this.service = service;
            this.cost = cost;
            this.etd = etd;
        }
    }

    public ShippingCostResponseDTO(String courier, List<ShippingCostDetail> detail) {
        this.couriers = courier;
        this.details = detail;
    }
}
