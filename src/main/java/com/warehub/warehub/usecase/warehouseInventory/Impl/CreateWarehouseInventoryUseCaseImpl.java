package com.warehub.warehub.usecase.warehouseInventory.Impl;

import com.warehub.warehub.common.exceptions.DuplicateWarehouseInventoryException;
import com.warehub.warehub.common.exceptions.ProductNotFoundException;
import com.warehub.warehub.common.exceptions.WarehouseInventoryStatusNotFoundException;
import com.warehub.warehub.common.exceptions.WarehouseNotFoundException;
import com.warehub.warehub.entity.Product;
import com.warehub.warehub.entity.Warehouse;
import com.warehub.warehub.entity.WarehouseInventoryStatus;
import com.warehub.warehub.infrastructure.product.repository.ProductRepository;
import com.warehub.warehub.infrastructure.warehouse.repository.WarehouseRepository;
import com.warehub.warehub.infrastructure.warehouseInventory.dto.WarehouseInventoryRequestDTO;
import com.warehub.warehub.infrastructure.warehouseInventory.dto.WarehouseInventoryResponseDTO;
import com.warehub.warehub.infrastructure.warehouseInventory.repository.WarehouseInventoryRepository;
import com.warehub.warehub.infrastructure.warehouseInventory.repository.WarehouseInventoryStatusRepository;
import com.warehub.warehub.usecase.warehouseInventory.CreateWarehouseInventoryUseCase;
import org.springframework.stereotype.Service;

@Service
public class CreateWarehouseInventoryUseCaseImpl implements CreateWarehouseInventoryUseCase {

    private final WarehouseInventoryRepository warehouseInventoryRepository;
    private final WarehouseInventoryStatusRepository warehouseInventoryStatusRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    public CreateWarehouseInventoryUseCaseImpl(WarehouseInventoryRepository warehouseInventoryRepository, WarehouseInventoryStatusRepository warehouseInventoryStatusRepository, ProductRepository productRepository, WarehouseRepository warehouseRepository) {
        this.warehouseInventoryRepository = warehouseInventoryRepository;
        this.warehouseInventoryStatusRepository = warehouseInventoryStatusRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
    }

    @Override
    public WarehouseInventoryResponseDTO createWarehouseInventory(WarehouseInventoryRequestDTO req) {
        boolean warehouseInventoryExist = warehouseInventoryRepository.existsByProductIdAndWarehouseIdAndDeletedAtIsNull(req.getProductId(), req.getWarehouseId());
        if (warehouseInventoryExist){
            throw new DuplicateWarehouseInventoryException("Warehouse Inventory with product ID " + req.getProductId() + " and warehouse ID " + req.getWarehouseId() + " already exist !");
        }
        Product product = productRepository.findByIdAndDeletedAtIsNull(req.getProductId())
                        .orElseThrow(()-> new ProductNotFoundException("Product with ID "+ req.getProductId() + " not found !"));

        Warehouse warehouse = warehouseRepository.findByIdAndDeletedAtIsNull(req.getWarehouseId())
                        .orElseThrow(()-> new WarehouseNotFoundException("Warehouse with ID "+ req.getWarehouseId() + " not found !"));

        WarehouseInventoryStatus warehouseInventoryStatus = warehouseInventoryStatusRepository.findByIdAndDeletedAtIsNull(1L)
                .orElseThrow(()-> new WarehouseInventoryStatusNotFoundException("Warehouse inventory status with ID "+ 1 + "is not found !"));

        return new WarehouseInventoryResponseDTO(warehouseInventoryRepository.save(req.toEntity(product, warehouse, warehouseInventoryStatus)));
    }
}
