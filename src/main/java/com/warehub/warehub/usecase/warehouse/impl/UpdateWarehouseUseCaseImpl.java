package com.warehub.warehub.usecase.warehouse.impl;

import com.warehub.warehub.common.exceptions.WarehouseNotFoundException;
import com.warehub.warehub.entity.Warehouse;
import com.warehub.warehub.infrastructure.warehouse.dto.WarehouseRequestDTO;
import com.warehub.warehub.infrastructure.warehouse.dto.WarehouseResponseDTO;
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
    public WarehouseResponseDTO updateWarehouse(WarehouseRequestDTO req) {
        Warehouse warehouse = warehouseRepository.findById(req.getId())
                .orElseThrow(()-> new WarehouseNotFoundException("Warehouse with ID "+ req.getId() + " not found !"));

        return new WarehouseResponseDTO(warehouseRepository.save(req.toEntity()));
    }
}
