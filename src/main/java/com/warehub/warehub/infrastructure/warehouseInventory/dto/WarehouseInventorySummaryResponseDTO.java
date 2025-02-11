package com.warehub.warehub.infrastructure.warehouseInventory.dto;

import com.warehub.warehub.entity.WarehouseInventory;
import com.warehub.warehub.infrastructure.product.dto.ProductSummaryResponseDTO;
import com.warehub.warehub.infrastructure.warehouse.dto.WarehouseResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseInventorySummaryResponseDTO {
    private Long id;
    private ProductSummaryResponseDTO product;
    private Integer quantity;
    private WarehouseInventoryStatusResponseDTO status;
    private WarehouseResponseDTO warehouse;

    public WarehouseInventorySummaryResponseDTO(WarehouseInventory warehouseInventory, String thumbnail){
        this.id = warehouseInventory.getId();
        this.product = new ProductSummaryResponseDTO(warehouseInventory.getProduct(), thumbnail);
        this.quantity = warehouseInventory.getQuantity();
        this.status = new WarehouseInventoryStatusResponseDTO(warehouseInventory.getWarehouseInventoryStatus());
        this.warehouse = new WarehouseResponseDTO(warehouseInventory.getWarehouse());
    }
}