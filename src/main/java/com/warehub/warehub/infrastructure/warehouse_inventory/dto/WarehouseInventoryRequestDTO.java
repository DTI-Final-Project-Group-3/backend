package com.warehub.warehub.infrastructure.warehouse_inventory.dto;

import com.warehub.warehub.entity.Product;
import com.warehub.warehub.entity.Warehouse;
import com.warehub.warehub.entity.WarehouseInventory;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WarehouseInventoryRequestDTO {
    @NotNull
    private Long productId;

    @NotNull
    private Long warehouseId;

    @NotNull
    private Integer quantity;

    public WarehouseInventory toEntity(Product product, Warehouse warehouse){
        WarehouseInventory warehouseInventory = new WarehouseInventory();
        warehouseInventory.setProduct(product);
        warehouseInventory.setWarehouse(warehouse);
        warehouseInventory.setQuantity(this.quantity);
        return warehouseInventory;
    }
}
