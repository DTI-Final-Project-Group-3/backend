package com.warehub.warehub.usecase.warehouse_inventory.Impl;

import com.warehub.warehub.common.exceptions.WarehouseInventoryNotFoundException;
import com.warehub.warehub.entity.WarehouseInventory;
import com.warehub.warehub.infrastructure.warehouse_inventory.repository.WarehouseInventoryRepository;
import com.warehub.warehub.usecase.warehouse_inventory.DeleteWarehouseInventoryUseCase;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class DeleteWarehouseInventoryUseCaseImpl implements DeleteWarehouseInventoryUseCase {
    private final WarehouseInventoryRepository warehouseInventoryRepository;

    public DeleteWarehouseInventoryUseCaseImpl(WarehouseInventoryRepository warehouseInventoryRepository) {
        this.warehouseInventoryRepository = warehouseInventoryRepository;
    }

    @Override
    public void deletedWarehouseInventoryById(Long warehouseInventoryId) {
        WarehouseInventory warehouseInventory = warehouseInventoryRepository.findByIdAndDeletedAtIsNull(warehouseInventoryId)
                .orElseThrow(()-> new WarehouseInventoryNotFoundException("Warehouse inventory with ID " + warehouseInventoryId + "not found !"));

        warehouseInventory.setDeletedAt(OffsetDateTime.now());
        warehouseInventoryRepository.save(warehouseInventory);
    }
}
