package com.warehub.warehub.infrastructure.warehouse_inventory.dto;

import com.warehub.warehub.entity.WarehouseInventory;
import lombok.Data;


@Data
public class WarehouseInventoryResponseDTO {
    private Long id;
    private Long productId;
    private String productName;
    private Long warehouseId;
    private String warehouseName;
    private Integer quantity;
    private Long statusId;
    private String statusName;

    public WarehouseInventoryResponseDTO(WarehouseInventory warehouseInventory){
        this.id = warehouseInventory.getId();
        this.productId = warehouseInventory.getProduct().getId();
        this.productName = warehouseInventory.getProduct().getName();
        this.warehouseId = warehouseInventory.getWarehouse().getId();
        this.warehouseName = warehouseInventory.getWarehouse().getName();
        this.quantity = warehouseInventory.getQuantity();
        this.statusId = warehouseInventory.getWarehouseInventoryStatus().getId();
        this.statusName = warehouseInventory.getWarehouseInventoryStatus().getName();
    }
}
