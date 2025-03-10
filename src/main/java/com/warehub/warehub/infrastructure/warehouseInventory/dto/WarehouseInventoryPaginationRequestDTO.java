package com.warehub.warehub.infrastructure.warehouseInventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseInventoryPaginationRequestDTO {
    private int page;
    private int limit;
    private Long warehouseId;
    private String searchQuery;

}
