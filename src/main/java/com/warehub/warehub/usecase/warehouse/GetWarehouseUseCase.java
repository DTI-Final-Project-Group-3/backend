package com.warehub.warehub.usecase.warehouse;

import com.warehub.warehub.infrastructure.warehouse.dto.NearbyWarehouseRequestDTO;
import com.warehub.warehub.infrastructure.warehouse.dto.NearbyWarehouseResponseDTO;
import com.warehub.warehub.infrastructure.warehouse.dto.WarehouseResponseDTO;

import java.util.List;

public interface GetWarehouseUseCase {
    List<WarehouseResponseDTO> getAllWarehouse();
    WarehouseResponseDTO getWarehouseById(Long warehouseId);
    List<NearbyWarehouseResponseDTO> getNearbyWarehouses(NearbyWarehouseRequestDTO req);
}
