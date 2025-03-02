package com.warehub.warehub.usecase.warehouse;

import com.warehub.warehub.infrastructure.warehouse.dto.NearbyWarehouseQuantityResponseDTO;
import com.warehub.warehub.infrastructure.warehouse.dto.NearbyWarehouseRequestDTO;
import com.warehub.warehub.infrastructure.warehouse.dto.NearbyWarehouseResponseDTO;
import com.warehub.warehub.infrastructure.warehouse.dto.WarehouseDetailResponseDTO;

import java.util.List;

public interface GetWarehouseUseCase {
    List<WarehouseDetailResponseDTO> getAllWarehouse();
    WarehouseDetailResponseDTO getWarehouseById(Long warehouseId);
    List<NearbyWarehouseResponseDTO> getNearbyWarehouses(NearbyWarehouseRequestDTO req);
    List<NearbyWarehouseQuantityResponseDTO> getNearbyWarehouseByProductId(Long warehouseId, Long productId);
}
