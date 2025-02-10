package com.warehub.warehub.usecase.warehouseInventory;

import com.warehub.warehub.infrastructure.warehouseInventory.dto.WarehouseInventoryRequestDTO;
import com.warehub.warehub.infrastructure.warehouseInventory.dto.WarehouseInventoryResponseDTO;

public interface UpdateWarehouseInventoryUseCase {
    WarehouseInventoryResponseDTO updateQuantity(Long warehouseInventoryId, WarehouseInventoryRequestDTO req);
}
