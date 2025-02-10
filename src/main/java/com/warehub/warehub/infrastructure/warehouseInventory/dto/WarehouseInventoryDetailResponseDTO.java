package com.warehub.warehub.infrastructure.warehouseInventory.dto;

import com.warehub.warehub.entity.WarehouseInventory;
import com.warehub.warehub.infrastructure.product.dto.ProductCategoryResponseDTO;
import com.warehub.warehub.infrastructure.product.dto.ProductImageResponseDTO;
import com.warehub.warehub.infrastructure.product.dto.ProductDetailResponseDTO;
import com.warehub.warehub.infrastructure.warehouse.dto.WarehouseResponseDTO;
import lombok.Data;

import java.util.List;

@Data
public class WarehouseInventoryDetailResponseDTO {
    private Long id;
    private ProductDetailResponseDTO product;
    private Integer quantity;
    private WarehouseInventoryStatusResponseDTO status;
    private WarehouseResponseDTO warehouse;

    public WarehouseInventoryDetailResponseDTO(WarehouseInventory warehouseInventory, List<ProductImageResponseDTO> productImageResponseDTO){
        this.id = warehouseInventory.getId();
        this.product = new ProductDetailResponseDTO(warehouseInventory.getProduct(), productImageResponseDTO);
        this.quantity = warehouseInventory.getQuantity();
        this.status = new WarehouseInventoryStatusResponseDTO(warehouseInventory.getWarehouseInventoryStatus());
        this.warehouse = new WarehouseResponseDTO(warehouseInventory.getWarehouse());
    }
}
