package com.warehub.warehub.infrastructure.warehouse.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NearbyWarehouseRequestDTO {

    @NotNull
    private Double longitude;

    @NotNull
    private Double latitude;

    private Long productId;

}