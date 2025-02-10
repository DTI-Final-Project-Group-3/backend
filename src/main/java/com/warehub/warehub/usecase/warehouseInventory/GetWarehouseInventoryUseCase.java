package com.warehub.warehub.usecase.warehouseInventory;

import com.warehub.warehub.common.utils.PaginationInfo;
import com.warehub.warehub.infrastructure.warehouseInventory.dto.WarehouseInventoryDetailResponseDTO;
import com.warehub.warehub.infrastructure.warehouseInventory.dto.WarehouseInventoryPaginationRequestDTO;
import com.warehub.warehub.infrastructure.warehouseInventory.dto.WarehouseInventorySummaryResponseDTO;
import com.warehub.warehub.infrastructure.warehouseInventory.dto.WarehouseInventoryResponseDTO;

import java.util.List;

public interface GetWarehouseInventoryUseCase {

    WarehouseInventoryDetailResponseDTO getDetailWarehouseInventoryById(Long warehouseInventoryId);

    List<WarehouseInventoryResponseDTO> getWarehouseInventoryByWarehouseId(Long warehouseId);

    PaginationInfo<WarehouseInventorySummaryResponseDTO> getPaginatedWarehouseInventory(WarehouseInventoryPaginationRequestDTO req);


}
