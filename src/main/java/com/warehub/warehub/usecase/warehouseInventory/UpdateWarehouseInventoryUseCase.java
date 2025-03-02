package com.warehub.warehub.usecase.warehouseInventory;

import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationRequestDTO;
import com.warehub.warehub.infrastructure.warehouseInventory.dto.WarehouseInventoryResponseDTO;

public interface UpdateWarehouseInventoryUseCase {
    WarehouseInventoryResponseDTO updateQuantity(Long warehouseId, ProductMutationRequestDTO req);
}