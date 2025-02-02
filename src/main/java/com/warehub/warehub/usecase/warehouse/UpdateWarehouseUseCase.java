package com.warehub.warehub.usecase.warehouse;

import com.warehub.warehub.infrastructure.warehouse.dto.WarehouseRequestDTO;
import com.warehub.warehub.infrastructure.warehouse.dto.WarehouseResponseDTO;

public interface UpdateWarehouseUseCase {
    WarehouseResponseDTO updateWarehouse(WarehouseRequestDTO req);
}
