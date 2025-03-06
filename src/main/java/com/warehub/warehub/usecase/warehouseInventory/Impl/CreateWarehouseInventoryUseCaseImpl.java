package com.warehub.warehub.usecase.warehouseInventory.Impl;

import com.warehub.warehub.common.enums.MutationConstant;
import com.warehub.warehub.common.utils.ValidationService;
import com.warehub.warehub.entity.*;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationRequestDTO;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationRepository;
import com.warehub.warehub.infrastructure.warehouseInventory.dto.WarehouseInventoryResponseDTO;
import com.warehub.warehub.infrastructure.warehouseInventory.repository.WarehouseInventoryRepository;
import com.warehub.warehub.usecase.warehouseInventory.CreateWarehouseInventoryUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateWarehouseInventoryUseCaseImpl implements CreateWarehouseInventoryUseCase {

    private final ValidationService validationService;
    private final WarehouseInventoryRepository warehouseInventoryRepository;
    private final ProductMutationRepository productMutationRepository;

    public CreateWarehouseInventoryUseCaseImpl(ValidationService validationService, WarehouseInventoryRepository warehouseInventoryRepository, ProductMutationRepository productMutationRepository) {
        this.validationService = validationService;
        this.warehouseInventoryRepository = warehouseInventoryRepository;
        this.productMutationRepository = productMutationRepository;
    }

    @Override
    @Transactional
    public WarehouseInventoryResponseDTO createWarehouseInventory(ProductMutationRequestDTO req) {

        // validate request
        validationService.validateDuplicateWarehouseInventory(req.getProductId(), req.getDestinationWarehouseId());
        Product product = validationService.validateProductId(req.getProductId());
        Warehouse warehouse = validationService.validateWarehouseId(req.getDestinationWarehouseId(), "Destination warehouse");

        // create new inventory
        WarehouseInventory warehouseInventory = new WarehouseInventory();
        warehouseInventory.setWarehouse(warehouse);
        warehouseInventory.setProduct(product);
        warehouseInventory.setQuantity(req.getQuantity());
        warehouseInventoryRepository.save(warehouseInventory);

        // create new mutation record
        User requester = validationService.validateUserId(req.getRequesterId());
        ProductMutationType productMutationType = validationService.validateProductMutationTypeId(MutationConstant.TYPE_CREATE_INVENTORY.getValue());
        ProductMutationStatus productMutationStatus = validationService.validateProductMutationStatusId(MutationConstant.STATUS_COMPLETED.getValue());

        ProductMutation productMutation = new ProductMutation();
        productMutation.setProduct(product);
        productMutation.setQuantity(req.getQuantity());
        productMutation.setRequester(requester);
        productMutation.setRequesterNotes(req.getRequesterNotes());
        productMutation.setDestinationWarehouse(warehouse);
        productMutation.setProductMutationType(productMutationType);
        productMutation.setProductMutationStatus(productMutationStatus);
        productMutationRepository.save(productMutation);

        return new WarehouseInventoryResponseDTO(warehouseInventory);
    }
}
