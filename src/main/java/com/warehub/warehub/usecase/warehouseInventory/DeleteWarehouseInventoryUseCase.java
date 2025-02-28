package com.warehub.warehub.usecase.warehouseInventory;

import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationProcessRequestDTO;

public interface DeleteWarehouseInventoryUseCase {
    void deletedWarehouseInventoryById(Long warehouseInventoryId, ProductMutationProcessRequestDTO req);
}
