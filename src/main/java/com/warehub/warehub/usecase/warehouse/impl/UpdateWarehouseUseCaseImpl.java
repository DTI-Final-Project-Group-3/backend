package com.warehub.warehub.usecase.warehouse.impl;

import com.warehub.warehub.common.exceptions.WarehouseNotFoundException;
import com.warehub.warehub.entity.Warehouse;
import com.warehub.warehub.infrastructure.warehouse.dto.WarehouseRequestDTO;
import com.warehub.warehub.infrastructure.warehouse.dto.WarehouseDetailResponseDTO;
import com.warehub.warehub.infrastructure.warehouse.repository.WarehouseRepository;
import com.warehub.warehub.usecase.warehouse.UpdateWarehouseUseCase;
import org.springframework.stereotype.Service;

@Service
public class UpdateWarehouseUseCaseImpl implements UpdateWarehouseUseCase {

    private final WarehouseRepository warehouseRepository;

    public UpdateWarehouseUseCaseImpl(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    @Override
    public WarehouseDetailResponseDTO updateWarehouse(Long warehouseId, WarehouseRequestDTO req) {
        Warehouse warehouse = warehouseRepository.findByIdAndDeletedAtIsNull(warehouseId)
                .orElseThrow(()-> new WarehouseNotFoundException("Warehouse with ID "+ warehouseId + " not found !"));

        Warehouse updatedWarehouse = req.toEntity();
        updatedWarehouse.setId(warehouseId);

        return new WarehouseDetailResponseDTO(warehouseRepository.save(updatedWarehouse));
    }
}
