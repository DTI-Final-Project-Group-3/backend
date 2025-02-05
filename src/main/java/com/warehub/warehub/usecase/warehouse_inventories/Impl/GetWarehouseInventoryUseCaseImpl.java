package com.warehub.warehub.usecase.warehouse_inventories.Impl;

import com.warehub.warehub.common.exceptions.WarehouseInventoryNotFoundException;
import com.warehub.warehub.entity.WarehouseInventory;
import com.warehub.warehub.infrastructure.product.dto.ProductImageResponseDTO;
import com.warehub.warehub.infrastructure.product.repository.ProductImageRepository;
import com.warehub.warehub.infrastructure.warehouse_inventories.dto.DetailWarehouseInventoryResponseDTO;
import com.warehub.warehub.infrastructure.warehouse_inventories.dto.WarehouseInventoryResponseDTO;
import com.warehub.warehub.infrastructure.warehouse_inventories.repository.WarehouseInventoryRepository;
import com.warehub.warehub.usecase.warehouse_inventories.GetWarehouseInventoryUseCase;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetWarehouseInventoryUseCaseImpl implements GetWarehouseInventoryUseCase {

    private final WarehouseInventoryRepository warehouseInventoryRepository;
    private final ProductImageRepository productImageRepository;

    public GetWarehouseInventoryUseCaseImpl(WarehouseInventoryRepository warehouseInventoryRepository, ProductImageRepository productImageRepository) {
        this.warehouseInventoryRepository = warehouseInventoryRepository;
        this.productImageRepository = productImageRepository;
    }

    @Override
    public DetailWarehouseInventoryResponseDTO getDetailWarehouseInventoryById(Long warehouseInventoryId) {
        WarehouseInventory warehouseInventory = warehouseInventoryRepository.findByIdAndDeletedAtIsNull(warehouseInventoryId)
                .orElseThrow(()-> new WarehouseInventoryNotFoundException("Warehouse inventory with ID " + warehouseInventoryId + " not found !"));

        List<ProductImageResponseDTO> productImageResponseDTO = productImageRepository.findByProductIdAndDeletedAtIsNull(warehouseInventory.getProduct().getId())
                .stream().map(ProductImageResponseDTO::new).toList();

        return new DetailWarehouseInventoryResponseDTO(warehouseInventory, productImageResponseDTO);
    }

    @Override
    public List<WarehouseInventoryResponseDTO> getWarehouseInventoryByWarehouseId(Long warehouseId) {
        List<WarehouseInventory> warehouseInventories = warehouseInventoryRepository.findByWarehouseIdAndDeletedAtIsNull(warehouseId);

        return warehouseInventories.stream().map(WarehouseInventoryResponseDTO::new).toList();
    }
}
