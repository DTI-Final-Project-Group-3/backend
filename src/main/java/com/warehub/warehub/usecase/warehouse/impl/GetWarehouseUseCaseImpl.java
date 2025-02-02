package com.warehub.warehub.usecase.warehouse.impl;

import com.warehub.warehub.common.exceptions.WarehouseNotFoundException;
import com.warehub.warehub.entity.Warehouse;
import com.warehub.warehub.infrastructure.warehouse.dto.WarehouseResponseDTO;
import com.warehub.warehub.infrastructure.warehouse.repository.WarehouseRepository;
import com.warehub.warehub.usecase.warehouse.GetWarehouseUseCase;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetWarehouseUseCaseImpl implements GetWarehouseUseCase {

    private final WarehouseRepository warehouseRepository;

    public GetWarehouseUseCaseImpl(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    @Override
    public List<WarehouseResponseDTO> getAllWarehouse() {
        List<Warehouse> warehouses = warehouseRepository.findActiveAll();
        return warehouses.stream().map(WarehouseResponseDTO::new).toList();
    }

    @Override
    public WarehouseResponseDTO getWarehouseById(Long warehouseId) {
        Warehouse warehouse = warehouseRepository.findActiveById(warehouseId)
                .orElseThrow(()-> new WarehouseNotFoundException("Warehouse with ID " + warehouseId + " not found !"));

        return new WarehouseResponseDTO(warehouse);
    }

    @Override
    public List<WarehouseResponseDTO> getNearbyWarehouses(double longitude, double latitude, double maxDistanceInMeters) {
        List<Warehouse> nearbyWarehouses = warehouseRepository.findActiveNearbyWarehouses(longitude, latitude, maxDistanceInMeters);
        return nearbyWarehouses.stream().map(WarehouseResponseDTO::new).toList();
    }
}
