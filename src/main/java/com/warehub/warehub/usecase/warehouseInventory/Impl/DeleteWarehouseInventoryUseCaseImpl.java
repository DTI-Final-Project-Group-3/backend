package com.warehub.warehub.usecase.warehouseInventory.Impl;

import com.warehub.warehub.common.enums.MutationConstant;
import com.warehub.warehub.common.utils.ValidationService;
import com.warehub.warehub.entity.*;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationProcessRequestDTO;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationRepository;
import com.warehub.warehub.infrastructure.warehouseInventory.repository.WarehouseInventoryRepository;
import com.warehub.warehub.usecase.warehouseInventory.DeleteWarehouseInventoryUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class DeleteWarehouseInventoryUseCaseImpl implements DeleteWarehouseInventoryUseCase {

    private final ValidationService validationService;
    private final ProductMutationRepository productMutationRepository;
    private final WarehouseInventoryRepository warehouseInventoryRepository;

    public DeleteWarehouseInventoryUseCaseImpl(ValidationService validationService, ProductMutationRepository productMutationRepository, WarehouseInventoryRepository warehouseInventoryRepository) {
        this.validationService = validationService;
        this.productMutationRepository = productMutationRepository;
        this.warehouseInventoryRepository = warehouseInventoryRepository;
    }

    @Override
    @Transactional
    public void deletedWarehouseInventoryById(Long warehouseInventoryId, ProductMutationProcessRequestDTO req) {

        // Soft delete inventory
        WarehouseInventory warehouseInventory = validationService.validateWarehouseInventoryId(warehouseInventoryId);
        warehouseInventory.setDeletedAt(OffsetDateTime.now());
        warehouseInventoryRepository.save(warehouseInventory);

        // validate request
        User requester = validationService.validateUserId(req.getUserId());
        ProductMutationType productMutationType = validationService.validateProductMutationTypeId(MutationConstant.TYPE_DELETE_INVENTORY.getValue());
        ProductMutationStatus productMutationStatus = validationService.validateProductMutationStatusId(MutationConstant.STATUS_COMPLETED.getValue());

        Product product = warehouseInventory.getProduct();
        Warehouse destinationWarehouse = warehouseInventory.getWarehouse();
        Integer quantity = warehouseInventory.getQuantity() * -1;

        // create mutation
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
