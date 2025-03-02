package com.warehub.warehub.usecase.warehouseInventory.Impl;

import com.warehub.warehub.common.enums.MutationConstant;
import com.warehub.warehub.common.exceptions.*;
import com.warehub.warehub.entity.*;
import com.warehub.warehub.infrastructure.product.repository.ProductRepository;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationProcessRequestDTO;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationRepository;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationStatusRepository;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationTypeRepository;
import com.warehub.warehub.infrastructure.users.repository.UsersRepository;
import com.warehub.warehub.infrastructure.warehouse.repository.WarehouseRepository;
import com.warehub.warehub.infrastructure.warehouseInventory.repository.WarehouseInventoryRepository;
import com.warehub.warehub.usecase.warehouseInventory.DeleteWarehouseInventoryUseCase;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class DeleteWarehouseInventoryUseCaseImpl implements DeleteWarehouseInventoryUseCase {

    private final ProductMutationRepository productMutationRepository;
    private final ProductRepository productRepository;
    private final UsersRepository usersRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductMutationTypeRepository productMutationTypeRepository;
    private final ProductMutationStatusRepository productMutationStatusRepository;
    private final WarehouseInventoryRepository warehouseInventoryRepository;

    public DeleteWarehouseInventoryUseCaseImpl(ProductMutationRepository productMutationRepository, ProductRepository productRepository, UsersRepository usersRepository, WarehouseRepository warehouseRepository, ProductMutationTypeRepository productMutationTypeRepository, ProductMutationStatusRepository productMutationStatusRepository, WarehouseInventoryRepository warehouseInventoryRepository) {
        this.productMutationRepository = productMutationRepository;
        this.productRepository = productRepository;
        this.usersRepository = usersRepository;
        this.warehouseRepository = warehouseRepository;
        this.productMutationTypeRepository = productMutationTypeRepository;
        this.productMutationStatusRepository = productMutationStatusRepository;
        this.warehouseInventoryRepository = warehouseInventoryRepository;
    }

    @Override
    @Transactional
    public void deletedWarehouseInventoryById(Long warehouseInventoryId, ProductMutationProcessRequestDTO req) {

        // Soft delete inventory
        WarehouseInventory warehouseInventory = warehouseInventoryRepository.findByIdAndDeletedAtIsNull(warehouseInventoryId)
                .orElseThrow(()-> new WarehouseInventoryNotFoundException("Warehouse inventory with ID " + warehouseInventoryId + "not found !"));
        warehouseInventory.setDeletedAt(OffsetDateTime.now());
        warehouseInventoryRepository.save(warehouseInventory);

        // create mutation
        Long productId = warehouseInventory.getProduct().getId();
        Long warehouseId = warehouseInventory.getWarehouse().getId();
        Integer quantity = warehouseInventory.getQuantity() * -1;

        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(()-> new ProductNotFoundException("Product with ID " + productId + " not found !"));

        User requester = usersRepository.findByIdAndDeletedAtIsNull(req.getUserId())
                .orElseThrow(()-> new UsernameNotFoundException("User with ID " + req.getNotes() + " not found !"));

        Warehouse destinationWarehouse = warehouseRepository.findByIdAndDeletedAtIsNull(warehouseId)
                .orElseThrow(()-> new WarehouseNotFoundException("Warehouse with ID "+ warehouseId + " not found !"));

        ProductMutationType productMutationType = productMutationTypeRepository.findByIdAndDeletedAtIsNull(MutationConstant.TYPE_DELETE_INVENTORY.getValue())
                .orElseThrow(()-> new ProductMutationTypeNotFoundException("Product mutation type with ID not found !"));

        ProductMutationStatus productMutationStatus = productMutationStatusRepository.findByIdAndDeletedAtIsNull(MutationConstant.STATUS_COMPLETED.getValue())
                .orElseThrow(()-> new ProductMutationStatusNotFoundException("Product mutation status with ID not found !"));

        ProductMutation productMutation = new ProductMutation();
        productMutation.setProduct(product);
        productMutation.setQuantity(quantity);
        productMutation.setRequester(requester);
        productMutation.setRequesterNotes(req.getNotes());
        productMutation.setDestinationWarehouse(destinationWarehouse);
        productMutation.setProductMutationType(productMutationType);
        productMutation.setProductMutationStatus(productMutationStatus);
        productMutationRepository.save(productMutation);
    }
}