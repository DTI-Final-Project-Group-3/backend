package com.warehub.warehub.usecase.warehouse_inventories;

import com.warehub.warehub.infrastructure.warehouse_inventories.dto.WarehouseInventoryRequestDTO;
import com.warehub.warehub.infrastructure.warehouse_inventories.dto.WarehouseInventoryResponseDTO;

public interface UpdateWarehouseInventoryUseCase {
    WarehouseInventoryResponseDTO updateQuantity(Long warehouseInventoryId, WarehouseInventoryRequestDTO req);
}
