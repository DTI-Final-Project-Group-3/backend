package com.warehub.warehub.usecase.warehouse;

import com.warehub.warehub.infrastructure.warehouse.dto.WarehouseResponseDTO;

import java.util.List;

public interface GetWarehouseUseCase {
    List<WarehouseResponseDTO> getAllWarehouse();
    WarehouseResponseDTO getWarehouseById(Long warehouseId);
    List<WarehouseResponseDTO> getNearbyWarehouses(double longitude, double latitude, double maxDistanceInMeter);
}
