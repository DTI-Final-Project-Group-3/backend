package com.warehub.warehub.usecase.warehouse.impl;

import com.warehub.warehub.common.exceptions.WarehouseNotFoundException;
import com.warehub.warehub.entity.Warehouse;
import com.warehub.warehub.infrastructure.warehouse.dto.WarehouseResponseDTO;
import com.warehub.warehub.infrastructure.warehouse.repository.WarehouseRepository;
import com.warehub.warehub.usecase.warehouse.DeleteWarehouseUseCase;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class DeleteWarehouseUseCaseImpl implements DeleteWarehouseUseCase {
    private final WarehouseRepository warehouseRepository;

    public DeleteWarehouseUseCaseImpl(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    @Override
    public WarehouseResponseDTO deleteWarehouseById(Long warehouseId) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(()-> new WarehouseNotFoundException("Warehouse with ID "+ warehouseId + " not found !"));

        warehouse.setDeletedAt(OffsetDateTime.now());
        warehouseRepository.save(warehouse);

        return new WarehouseResponseDTO(warehouse);
    }
}
