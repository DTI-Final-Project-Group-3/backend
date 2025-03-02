package com.warehub.warehub.usecase.warehouseInventory;

import com.warehub.warehub.common.utils.PaginationInfo;
import com.warehub.warehub.infrastructure.warehouseInventory.dto.WarehouseInventoryPaginationRequestDTO;
import com.warehub.warehub.infrastructure.warehouseInventory.dto.WarehouseInventoryPaginationResponseDTO;


public interface GetWarehouseInventoryUseCase {

    PaginationInfo<WarehouseInventoryPaginationResponseDTO> getPaginatedWarehouseInventoryByWarehouseId(WarehouseInventoryPaginationRequestDTO req);
}