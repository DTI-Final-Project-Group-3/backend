package com.warehub.warehub.usecase.warehouse_inventories.Impl;

import com.warehub.warehub.common.exceptions.WarehouseInventoryNotFoundException;
import com.warehub.warehub.entity.WarehouseInventory;
import com.warehub.warehub.infrastructure.warehouse_inventories.dto.WarehouseInventoryResponseDTO;
import com.warehub.warehub.infrastructure.warehouse_inventories.repository.WarehouseInventoryRepository;
import com.warehub.warehub.usecase.warehouse_inventories.GetWarehouseInventoryUseCase;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetWarehouseInventoryUseCaseImpl implements GetWarehouseInventoryUseCase {

    private final WarehouseInventoryRepository warehouseInventoryRepository;

    public GetWarehouseInventoryUseCaseImpl(WarehouseInventoryRepository warehouseInventoryRepository) {
        this.warehouseInventoryRepository = warehouseInventoryRepository;
    }

    @Override
    public WarehouseInventoryResponseDTO getWarehouseInventoryById(Long warehouseInventoryId) {
        WarehouseInventory warehouseInventory = warehouseInventoryRepository.findByIdAndDeletedAtIsNull(warehouseInventoryId)
                .orElseThrow(()-> new WarehouseInventoryNotFoundException("Warehouse inventory with ID " + warehouseInventoryId + " not found !"));

        return new WarehouseInventoryResponseDTO(warehouseInventory);
    }

    @Override
    public List<WarehouseInventoryResponseDTO> getWarehouseInventoryByWarehouseId(Long warehouseId) {
        List<WarehouseInventory> warehouseInventories = warehouseInventoryRepository.findByWarehouseIdAndDeletedAtIsNull(warehouseId);

        return warehouseInventories.stream().map(WarehouseInventoryResponseDTO::new).toList();
    }
}
