package com.warehub.warehub.usecase.productMutation.impl;

import com.warehub.warehub.common.enums.MutationConstant;
import com.warehub.warehub.common.exceptions.*;
import com.warehub.warehub.common.utils.ValidationService;
import com.warehub.warehub.entity.*;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationRequestDTO;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationResponseDTO;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationRepository;
import com.warehub.warehub.infrastructure.warehouseInventory.repository.WarehouseInventoryRepository;
import com.warehub.warehub.usecase.productMutation.CreateProductMutationUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateProductMutationUseCaseImpl implements CreateProductMutationUseCase {

    private final ValidationService validationService;
    private final ProductMutationRepository productMutationRepository;
    private final WarehouseInventoryRepository warehouseInventoryRepository;

    public CreateProductMutationUseCaseImpl(ValidationService validationService, ProductMutationRepository productMutationRepository, WarehouseInventoryRepository warehouseInventoryRepository) {
        this.validationService = validationService;
        this.productMutationRepository = productMutationRepository;
        this.warehouseInventoryRepository = warehouseInventoryRepository;
    }

    @Override
    @Transactional
    public ProductMutationResponseDTO createManualMutation(ProductMutationRequestDTO req) {

        // validation request
        Product product = validationService.validateProductId(req.getProductId());
        User requester = validationService.validateUserId(req.getRequesterId());
        Warehouse originWarehouse = validationService.validateWarehouseId(req.getOriginWarehouseId(), "Origin warehouse");
        Warehouse destinationWarehouse = validationService.validateWarehouseId(req.getDestinationWarehouseId(), "Destination warehouse");
        ProductMutationType productMutationTypeManual = validationService.validateProductMutationTypeId(MutationConstant.TYPE_MANUAL_MUTATION.getValue());
        ProductMutationStatus productMutationStatusPending = validationService.validateProductMutationStatusId(MutationConstant.STATUS_PENDING.getValue());
        WarehouseInventory originWarehouseInventory = validationService.validateWarehouseInventoryByProductIdAndWarehouseId(req.getProductId(), req.getOriginWarehouseId());

        if (originWarehouseInventory.getQuantity() < req.getQuantity()){
            throw new NegativeQuantityException("Request quantity can't exceed available quantity !");
        }

        // reserve quantity from origin warehouse
        originWarehouseInventory.setQuantity(originWarehouseInventory.getQuantity() - req.getQuantity());
        warehouseInventoryRepository.save(originWarehouseInventory);

        // create product mutation
        ProductMutation productMutation = new ProductMutation();
        productMutation.setProduct(product);
        productMutation.setQuantity(req.getQuantity());
        productMutation.setRequesterNotes(req.getRequesterNotes());
        productMutation.setRequester(requester);
        productMutation.setOriginWarehouse(originWarehouse);
        productMutation.setDestinationWarehouse(destinationWarehouse);
        productMutation.setProductMutationType(productMutationTypeManual);
        productMutation.setProductMutationStatus(productMutationStatusPending);

        return new ProductMutationResponseDTO(productMutationRepository.save(productMutation));
    }

    @Override
    public ProductMutationResponseDTO createAutoMutation(ProductMutationRequestDTO req) {

        // validation request
        Product product = validationService.validateProductId(req.getProductId());
        User requester = validationService.validateUserId(req.getRequesterId());
        Warehouse destinationWarehouse = validationService.validateWarehouseId(req.getDestinationWarehouseId(), "Destination warehouse");
        ProductMutationType productMutationTypeAuto = validationService.validateProductMutationTypeId(MutationConstant.TYPE_AUTO_MUTATION.getValue());
        ProductMutationStatus productMutationStatusCompleted = validationService.validateProductMutationStatusId(MutationConstant.STATUS_COMPLETED.getValue());

        // create product mutation
        ProductMutation productMutation = new ProductMutation();
        productMutation.setProduct(product);
        productMutation.setQuantity(req.getQuantity());
        productMutation.setRequesterNotes(req.getRequesterNotes());
        productMutation.setRequester(requester);
        productMutation.setDestinationWarehouse(destinationWarehouse);
        productMutation.setProductMutationType(productMutationTypeAuto);
        productMutation.setProductMutationStatus(productMutationStatusCompleted);

        return new ProductMutationResponseDTO(productMutationRepository.save(productMutation));
    }
}
