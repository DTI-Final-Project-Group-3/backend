package com.warehub.warehub.infrastructure.warehouse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NearbyWarehouseQuantityResponseDTO {
    private Long id;
    private String name;
    private Double distanceInMeters;
    private Integer totalQuantity;
}
