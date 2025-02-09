package com.warehub.warehub.usecase.warehouse_inventory.Impl;

import com.warehub.warehub.common.exceptions.WarehouseInventoryNotFoundException;
import com.warehub.warehub.entity.WarehouseInventory;
import com.warehub.warehub.infrastructure.warehouse_inventory.dto.WarehouseInventoryRequestDTO;
import com.warehub.warehub.infrastructure.warehouse_inventory.dto.WarehouseInventoryResponseDTO;
import com.warehub.warehub.infrastructure.warehouse_inventory.repository.WarehouseInventoryRepository;
import com.warehub.warehub.usecase.warehouse_inventory.UpdateWarehouseInventoryUseCase;
import org.springframework.stereotype.Service;

@Service
public class UpdateWarehouseInventoryUseCaseImpl implements UpdateWarehouseInventoryUseCase {

    private final WarehouseInventoryRepository warehouseInventoryRepository;

    public UpdateWarehouseInventoryUseCaseImpl(WarehouseInventoryRepository warehouseInventoryRepository) {
        this.warehouseInventoryRepository = warehouseInventoryRepository;
    }

    @Override
    public WarehouseInventoryResponseDTO updateQuantity(Long warehouseInventoryId, WarehouseInventoryRequestDTO req) {
        WarehouseInventory warehouseInventory = warehouseInventoryRepository.findByIdAndDeletedAtIsNull(warehouseInventoryId)
                .orElseThrow(()-> new WarehouseInventoryNotFoundException("Warehouse inventory with ID "+ warehouseInventoryId + " not found !"));

        warehouseInventory.setQuantity(req.getQuantity());

        return new WarehouseInventoryResponseDTO(warehouseInventoryRepository.save(warehouseInventory));
    }
}
