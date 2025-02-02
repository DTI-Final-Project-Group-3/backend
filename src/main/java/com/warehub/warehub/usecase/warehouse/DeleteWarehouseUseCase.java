package com.warehub.warehub.usecase.warehouse;

import com.warehub.warehub.infrastructure.warehouse.dto.WarehouseResponseDTO;

public interface DeleteWarehouseUseCase {
    WarehouseResponseDTO deleteWarehouseById(Long warehouseId);
}
