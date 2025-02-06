package com.warehub.warehub.infrastructure.warehouseInventory.dto;

import com.warehub.warehub.entity.WarehouseInventory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedWarehouseInventoryResponseDTO {
    private Long warehouseInventoryId;
    private Long productId;
    private String productName;
    private BigDecimal price;
    private String imageUrl;
    private Integer stock;
    private String status;
    private Long warehouseId;
    private String warehouseName;

    public PaginatedWarehouseInventoryResponseDTO(WarehouseInventory warehouseInventory, String imageUrl){
        this.warehouseInventoryId = warehouseInventory.getId();
        this.productId = warehouseInventory.getProduct().getId();
        this.productName = warehouseInventory.getProduct().getName();
        this.price = warehouseInventory.getProduct().getPrice();;
        this.imageUrl = imageUrl;
        this.stock = warehouseInventory.getQuantity();
        this.status = warehouseInventory.getWarehouseInventoryStatus().getName();
        this.warehouseId = warehouseInventory.getWarehouse().getId();
        this.warehouseName = warehouseInventory.getWarehouse().getName();
    }
}
