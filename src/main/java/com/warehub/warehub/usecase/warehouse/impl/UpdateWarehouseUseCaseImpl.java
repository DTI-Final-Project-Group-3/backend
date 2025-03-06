package com.warehub.warehub.usecase.warehouse.impl;

import com.warehub.warehub.common.utils.ValidationService;
import com.warehub.warehub.entity.Warehouse;
import com.warehub.warehub.infrastructure.warehouse.dto.WarehouseRequestDTO;
import com.warehub.warehub.infrastructure.warehouse.dto.WarehouseDetailResponseDTO;
import com.warehub.warehub.infrastructure.warehouse.repository.WarehouseRepository;
import com.warehub.warehub.usecase.warehouse.UpdateWarehouseUseCase;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class UpdateWarehouseUseCaseImpl implements UpdateWarehouseUseCase {

    private final ValidationService validationService;
    private final WarehouseRepository warehouseRepository;

    public UpdateWarehouseUseCaseImpl(ValidationService validationService, WarehouseRepository warehouseRepository) {
        this.validationService = validationService;
        this.warehouseRepository = warehouseRepository;
    }

    @Override
    public WarehouseDetailResponseDTO updateWarehouse(Long warehouseId, WarehouseRequestDTO req) {

        validationService.validateWarehouseId(warehouseId, "Warehouse");

        Warehouse updatedWarehouse = req.toEntity();
        updatedWarehouse.setId(warehouseId);
        updatedWarehouse.setUpdatedAt(OffsetDateTime.now());

        return new WarehouseDetailResponseDTO(warehouseRepository.save(updatedWarehouse));
    }
}
