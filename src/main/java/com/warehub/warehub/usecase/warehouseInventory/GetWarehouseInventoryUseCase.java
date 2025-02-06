package com.warehub.warehub.usecase.warehouseInventory;

import com.warehub.warehub.common.utils.PaginationInfo;
import com.warehub.warehub.infrastructure.warehouseInventory.dto.DetailWarehouseInventoryResponseDTO;
import com.warehub.warehub.infrastructure.warehouseInventory.dto.PaginatedWarehouseInventoryRequestDTO;
import com.warehub.warehub.infrastructure.warehouseInventory.dto.PaginatedWarehouseInventoryResponseDTO;
import com.warehub.warehub.infrastructure.warehouseInventory.dto.WarehouseInventoryResponseDTO;

import java.util.List;

public interface GetWarehouseInventoryUseCase {

    DetailWarehouseInventoryResponseDTO getDetailWarehouseInventoryById(Long warehouseInventoryId);

    List<WarehouseInventoryResponseDTO> getWarehouseInventoryByWarehouseId(Long warehouseId);

    PaginationInfo<PaginatedWarehouseInventoryResponseDTO> getPaginatedWarehouseInventory(PaginatedWarehouseInventoryRequestDTO req);


}
