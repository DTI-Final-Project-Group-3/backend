package com.warehub.warehub.usecase.warehouse.impl;

import com.warehub.warehub.common.utils.ValidationService;
import com.warehub.warehub.entity.Warehouse;
import com.warehub.warehub.infrastructure.warehouse.repository.WarehouseRepository;
import com.warehub.warehub.usecase.warehouse.DeleteWarehouseUseCase;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class DeleteWarehouseUseCaseImpl implements DeleteWarehouseUseCase {

    private final ValidationService validationService;
    private final WarehouseRepository warehouseRepository;

    public DeleteWarehouseUseCaseImpl(ValidationService validationService, WarehouseRepository warehouseRepository) {
        this.validationService = validationService;
        this.warehouseRepository = warehouseRepository;
    }

    @Override
    public void deleteWarehouseById(Long warehouseId) {
        Warehouse warehouse = validationService.validateWarehouseId(warehouseId, "Warehouse");

        warehouse.setDeletedAt(OffsetDateTime.now());
        warehouseRepository.save(warehouse);
    }
}
