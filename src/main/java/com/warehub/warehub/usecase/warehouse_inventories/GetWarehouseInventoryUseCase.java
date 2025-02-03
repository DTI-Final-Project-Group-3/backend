package com.warehub.warehub.usecase.warehouse_inventories;

import com.warehub.warehub.infrastructure.warehouse_inventories.dto.WarehouseInventoryResponseDTO;

import java.util.List;

public interface GetWarehouseInventoryUseCase {
    WarehouseInventoryResponseDTO getWarehouseInventoryById(Long warehouseInventoryId);
    List<WarehouseInventoryResponseDTO> getWarehouseInventoryByWarehouseId(Long warehouseId);
}
