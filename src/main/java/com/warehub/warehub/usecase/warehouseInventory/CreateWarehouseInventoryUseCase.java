package com.warehub.warehub.usecase.warehouseInventory;

import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationRequestDTO;
import com.warehub.warehub.infrastructure.warehouseInventory.dto.WarehouseInventoryRequestDTO;
import com.warehub.warehub.infrastructure.warehouseInventory.dto.WarehouseInventoryResponseDTO;

public interface CreateWarehouseInventoryUseCase {
    WarehouseInventoryResponseDTO createWarehouseInventory(ProductMutationRequestDTO req);
}
