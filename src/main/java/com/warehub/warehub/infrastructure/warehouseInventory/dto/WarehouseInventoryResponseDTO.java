package com.warehub.warehub.infrastructure.warehouseInventory.dto;

import com.warehub.warehub.entity.WarehouseInventory;
import lombok.Data;


@Data
public class WarehouseInventoryResponseDTO {
    private Long id;
    private Long productId;
    private Long warehouseId;
    private Integer quantity;

    public WarehouseInventoryResponseDTO(WarehouseInventory warehouseInventory){
        this.id = warehouseInventory.getId();
        this.productId = warehouseInventory.getProduct().getId();
        this.warehouseId = warehouseInventory.getWarehouse().getId();
        this.quantity = warehouseInventory.getQuantity();
    }
}