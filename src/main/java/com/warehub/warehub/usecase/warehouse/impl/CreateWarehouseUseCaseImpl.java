package com.warehub.warehub.usecase.warehouse.impl;

import com.warehub.warehub.common.utils.ValidationService;
import com.warehub.warehub.entity.Warehouse;
import com.warehub.warehub.infrastructure.warehouse.dto.WarehouseRequestDTO;
import com.warehub.warehub.infrastructure.warehouse.dto.WarehouseDetailResponseDTO;
import com.warehub.warehub.infrastructure.warehouse.repository.WarehouseRepository;
import com.warehub.warehub.usecase.warehouse.CreateWarehouseUseCase;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CreateWarehouseUseCaseImpl implements CreateWarehouseUseCase {

    private final ValidationService validationService;
    private final WarehouseRepository warehouseRepository;

    public CreateWarehouseUseCaseImpl(ValidationService validationService, WarehouseRepository warehouseRepository) {
        this.validationService = validationService;
        this.warehouseRepository = warehouseRepository;
    }

    @Override
    public WarehouseDetailResponseDTO createWarehouse(WarehouseRequestDTO req) {

        validationService.validateDuplicateWarehouseName(req.getName());

        Warehouse newWarehouse = warehouseRepository.save(req.toEntity());

        return new WarehouseDetailResponseDTO(newWarehouse);
    }
}
