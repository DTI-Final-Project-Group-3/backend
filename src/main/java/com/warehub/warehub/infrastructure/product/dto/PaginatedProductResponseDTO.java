package com.warehub.warehub.infrastructure.product.dto;

import com.warehub.warehub.entity.WarehouseInventory;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class PaginatedProductResponseDTO {
    private Long warehouseInventoryId;
    private Long warehouseId;
    private String warehouseName;
    private Long productId;
    private String productName;
    private BigDecimal price;
    private String imageUrl;
    private Integer stockQuantity;

    public PaginatedProductResponseDTO(WarehouseInventory warehouseInventory, String imageUrl){
        this.warehouseInventoryId = warehouseInventory.getId();
        this.warehouseId = warehouseInventory.getWarehouse().getId();
        this.warehouseName = warehouseInventory.getWarehouse().getName();
        this.productId = warehouseInventory.getProduct().getId();
        this.productName = warehouseInventory.getProduct().getName();
        this.price = warehouseInventory.getProduct().getPrice();;
        this.imageUrl = imageUrl;
        this.stockQuantity = warehouseInventory.getQuantity();
    }
}
