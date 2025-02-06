package com.warehub.warehub.infrastructure.warehouseInventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedWarehouseInventoryRequestDTO {
    private int page;
    private int limit;
    private Double longitude;
    private Double latitude;
    private Long productCategoryId;
    private String searchQuery;
}
