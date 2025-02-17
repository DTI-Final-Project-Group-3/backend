package com.warehub.warehub.infrastructure.warehouseInventory.dto;

import com.warehub.warehub.entity.WarehouseInventoryStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseInventoryStatusResponseDTO {
    private Long id;
    private String name;

    public WarehouseInventoryStatusResponseDTO(WarehouseInventoryStatus warehouseInventoryStatus){
        this.id = warehouseInventoryStatus.getId();
        this.name = warehouseInventoryStatus.getName();
    }
}
