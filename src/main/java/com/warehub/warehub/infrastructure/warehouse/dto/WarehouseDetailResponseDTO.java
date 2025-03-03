package com.warehub.warehub.infrastructure.warehouse.dto;

import com.warehub.warehub.entity.Warehouse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseDetailResponseDTO {
    private Long id;
    private String name;
    private String detailAddress;
    private double longitude;
    private double latitude;
    private String description;

    public WarehouseDetailResponseDTO(Warehouse warehouse){
        this.id = warehouse.getId();
        this.name = warehouse.getName();
        this.detailAddress = warehouse.getDetailAddress();
        this.longitude = warehouse.getLocation().getX();
        this.latitude = warehouse.getLocation().getY();
        this.description = warehouse.getDescriptions();
    }
}