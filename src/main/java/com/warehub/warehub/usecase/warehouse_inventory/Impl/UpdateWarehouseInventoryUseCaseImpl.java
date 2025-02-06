package com.warehub.warehub.usecase.warehouse_inventory.Impl;

import com.warehub.warehub.common.exceptions.NegativeQuantityException;
import com.warehub.warehub.common.exceptions.WarehouseInventoryNotFoundException;
import com.warehub.warehub.common.exceptions.WarehouseInventoryStatusNotFoundException;
import com.warehub.warehub.entity.WarehouseInventory;
import com.warehub.warehub.entity.WarehouseInventoryStatus;
import com.warehub.warehub.infrastructure.warehouse_inventory.dto.WarehouseInventoryRequestDTO;
import com.warehub.warehub.infrastructure.warehouse_inventory.dto.WarehouseInventoryResponseDTO;
import com.warehub.warehub.infrastructure.warehouse_inventory.repository.WarehouseInventoryRepository;
import com.warehub.warehub.infrastructure.warehouse_inventory.repository.WarehouseInventoryStatusRepository;
import com.warehub.warehub.usecase.warehouse_inventory.UpdateWarehouseInventoryUseCase;
import org.springframework.stereotype.Service;

@Service
public class UpdateWarehouseInventoryUseCaseImpl implements UpdateWarehouseInventoryUseCase {

    private final WarehouseInventoryRepository warehouseInventoryRepository;
    private final WarehouseInventoryStatusRepository warehouseInventoryStatusRepository;

    public UpdateWarehouseInventoryUseCaseImpl(WarehouseInventoryRepository warehouseInventoryRepository, WarehouseInventoryStatusRepository warehouseInventoryStatusRepository) {
        this.warehouseInventoryRepository = warehouseInventoryRepository;
        this.warehouseInventoryStatusRepository = warehouseInventoryStatusRepository;
    }

    @Override
    public WarehouseInventoryResponseDTO updateQuantity(Long warehouseInventoryId, WarehouseInventoryRequestDTO req) {
        WarehouseInventory warehouseInventory = warehouseInventoryRepository.findByIdAndDeletedAtIsNull(warehouseInventoryId)
                .orElseThrow(()-> new WarehouseInventoryNotFoundException("Warehouse inventory with ID "+ warehouseInventoryId + " not found !"));

        Integer updateQuantity = warehouseInventory.getQuantity() + req.getQuantity();
        if (updateQuantity < 0){
            throw new NegativeQuantityException("Quantity cannot be negative: " + updateQuantity);
        }

        Long status = (updateQuantity== 0) ? 2L : 1L;

        WarehouseInventoryStatus warehouseInventoryStatus = warehouseInventoryStatusRepository.findByIdAndDeletedAtIsNull(status)
                        .orElseThrow(()-> new WarehouseInventoryStatusNotFoundException("Warehouse inventory with status ID "+ status + " not found !"));

        warehouseInventory.setQuantity(updateQuantity);
        warehouseInventory.setWarehouseInventoryStatus(warehouseInventoryStatus);

        return new WarehouseInventoryResponseDTO(warehouseInventoryRepository.save(warehouseInventory));
    }
}
