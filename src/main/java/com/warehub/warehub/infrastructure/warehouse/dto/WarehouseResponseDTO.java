package com.warehub.warehub.infrastructure.warehouse.dto;

import com.warehub.warehub.entity.Warehouse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WarehouseResponseDTO {
    private Long id;
    private String name;

    public WarehouseResponseDTO(Warehouse warehouse){
        this.id = warehouse.getId();
        this.name = warehouse.getName();
    }
}
