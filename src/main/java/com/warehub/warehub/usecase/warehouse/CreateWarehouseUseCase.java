package com.warehub.warehub.usecase.warehouse;

import com.warehub.warehub.infrastructure.warehouse.dto.WarehouseRequestDTO;
import com.warehub.warehub.infrastructure.warehouse.dto.WarehouseDetailResponseDTO;

public interface CreateWarehouseUseCase {
    WarehouseDetailResponseDTO createWarehouse(WarehouseRequestDTO req);
}
