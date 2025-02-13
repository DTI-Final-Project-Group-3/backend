package com.warehub.warehub.infrastructure.warehouseInventory.dto;

import com.warehub.warehub.entity.WarehouseInventory;
import com.warehub.warehub.infrastructure.product.dto.ProductSummaryResponseDTO;
import com.warehub.warehub.infrastructure.warehouse.dto.WarehouseResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseInventorySummaryResponseDTO {
    private Long id;
    private ProductSummaryResponseDTO product;
    private Integer quantity;
    private WarehouseInventoryStatusResponseDTO status;
    private WarehouseResponseDTO warehouse;

    public WarehouseInventorySummaryResponseDTO(
                Long warehouseInventoryId,  // wi.id
                Long productId,             // p.id
                String productName,         // p.name
                BigDecimal productPrice,    // p.price
                Long categoryId,            // p.productCategory.id
                String categoryName,        // p.productCategory.name
                String imageUrl,            // pi.url
                Integer quantity,           // wi.quantity
                Long statusId,             // wis.id
                String statusName,          // wis.name
                Long warehouseId,          // w.id
                String warehouseName       // w.name
        ) {
            this.id = warehouseInventoryId;
            this.product = new ProductSummaryResponseDTO(productId, productName, productPrice, categoryId, categoryName, imageUrl);
            this.quantity = quantity;
            this.status = new WarehouseInventoryStatusResponseDTO(statusId, statusName);
            this.warehouse = new WarehouseResponseDTO(warehouseId, warehouseName);
        }
}