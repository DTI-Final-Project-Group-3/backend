package com.warehub.warehub.infrastructure.warehouse_inventory.dto;

import com.warehub.warehub.entity.WarehouseInventory;
import com.warehub.warehub.infrastructure.product.dto.ProductImageResponseDTO;
import com.warehub.warehub.infrastructure.product.dto.ProductResponseDTO;
import lombok.Data;

import java.util.List;

@Data
public class DetailWarehouseInventoryResponseDTO {
    private Long id;
    private ProductResponseDTO product;
    private Long warehouseId;
    private String warehouseName;
    private Integer quantity;
    private Long statusId;
    private String statusName;

    public DetailWarehouseInventoryResponseDTO(WarehouseInventory warehouseInventory, List<ProductImageResponseDTO> productImageResponseDTO){
        this.id = warehouseInventory.getId();
        this.product = new ProductResponseDTO(warehouseInventory.getProduct(), productImageResponseDTO);
        this.warehouseId = warehouseInventory.getWarehouse().getId();
        this.warehouseName = warehouseInventory.getWarehouse().getName();
        this.quantity = warehouseInventory.getQuantity();
        this.statusId = warehouseInventory.getWarehouseInventoryStatus().getId();
        this.statusName = warehouseInventory.getWarehouseInventoryStatus().getName();
    }
}
