package com.warehub.warehub.usecase.warehouse_inventory;

import com.warehub.warehub.common.utils.PaginationInfo;
import com.warehub.warehub.infrastructure.warehouse_inventory.dto.DetailWarehouseInventoryResponseDTO;
import com.warehub.warehub.infrastructure.warehouse_inventory.dto.PaginatedWarehouseInventoryRequestDTO;
import com.warehub.warehub.infrastructure.warehouse_inventory.dto.PaginatedWarehouseInventoryResponseDTO;
import com.warehub.warehub.infrastructure.warehouse_inventory.dto.WarehouseInventoryResponseDTO;

import java.util.List;

public interface GetWarehouseInventoryUseCase {

    DetailWarehouseInventoryResponseDTO getDetailWarehouseInventoryById(Long warehouseInventoryId);

    List<WarehouseInventoryResponseDTO> getWarehouseInventoryByWarehouseId(Long warehouseId);

    PaginationInfo<PaginatedWarehouseInventoryResponseDTO> getPaginatedWarehouseInventory(PaginatedWarehouseInventoryRequestDTO req);


}
