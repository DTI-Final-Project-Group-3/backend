package com.warehub.warehub.infrastructure.warehouseInventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseInventoryPaginationResponseDTO {
    private Long id;
    private Long productId;
    private String productName;
    private BigDecimal productPrice;
    private Long productCategoryId;
    private String productCategoryName;
    private String productThumbnail;
    private Integer quantity;
}
