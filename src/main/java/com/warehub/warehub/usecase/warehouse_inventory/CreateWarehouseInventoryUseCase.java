package com.warehub.warehub.usecase.warehouse_inventory;

import com.warehub.warehub.infrastructure.warehouse_inventory.dto.WarehouseInventoryRequestDTO;
import com.warehub.warehub.infrastructure.warehouse_inventory.dto.WarehouseInventoryResponseDTO;

public interface CreateWarehouseInventoryUseCase {
    WarehouseInventoryResponseDTO createWarehouseInventory(WarehouseInventoryRequestDTO req);
}
