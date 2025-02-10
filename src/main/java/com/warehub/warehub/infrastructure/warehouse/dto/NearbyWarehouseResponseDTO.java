package com.warehub.warehub.infrastructure.warehouse.dto;

import lombok.Data;

@Data
public class NearbyWarehouseResponseDTO {
    private Long id;
    private String name;
    private Double longitude;
    private Double latitude;
    private Double distanceInMeters;
}
