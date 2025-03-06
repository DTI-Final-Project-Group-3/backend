package com.warehub.warehub.usecase.warehouseInventory.Impl;

import com.warehub.warehub.common.enums.MutationConstant;
import com.warehub.warehub.common.exceptions.*;
import com.warehub.warehub.common.utils.ValidationService;
import com.warehub.warehub.entity.*;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationRequestDTO;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationRepository;
import com.warehub.warehub.infrastructure.warehouseInventory.dto.WarehouseInventoryResponseDTO;
import com.warehub.warehub.infrastructure.warehouseInventory.repository.WarehouseInventoryRepository;
import com.warehub.warehub.usecase.warehouseInventory.UpdateWarehouseInventoryUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateWarehouseInventoryUseCaseImpl implements UpdateWarehouseInventoryUseCase {

    private final ValidationService validationService;
    private final WarehouseInventoryRepository warehouseInventoryRepository;
    private final ProductMutationRepository productMutationRepository;

    public UpdateWarehouseInventoryUseCaseImpl(ValidationService validationService, WarehouseInventoryRepository warehouseInventoryRepository, ProductMutationRepository productMutationRepository) {
        this.validationService = validationService;
        this.warehouseInventoryRepository = warehouseInventoryRepository;
        this.productMutationRepository = productMutationRepository;
    }

    @Override
    @Transactional
    public WarehouseInventoryResponseDTO updateQuantity(Long warehouseInventoryId, ProductMutationRequestDTO req) {
        WarehouseInventory warehouseInventory = validationService.validateWarehouseInventoryId(warehouseInventoryId);

        // update quantity on inventory
        Integer updateQuantity = warehouseInventory.getQuantity() + req.getQuantity();

        if (updateQuantity < 0){
            throw new NegativeQuantityException("Quantity cannot be negative: " + updateQuantity);
        }
        warehouseInventory.setQuantity(updateQuantity);

        // create journal on product mutation
        Product product = validationService.validateProductId(req.getProductId());
        User requester = validationService.validateUserId(req.getRequesterId());
        Warehouse destinationWarehouse = validationService.validateWarehouseId(req.getDestinationWarehouseId(), "Destination warehouse");
        ProductMutationType productMutationType = validationService.validateProductMutationTypeId(MutationConstant.TYPE_UPDATE_INVENTORY.getValue());
        ProductMutationStatus productMutationStatus = validationService.validateProductMutationStatusId(MutationConstant.STATUS_COMPLETED.getValue());

        productMutationRepository.save(req.toEntity(product, requester, destinationWarehouse, productMutationStatus, productMutationType));

        return new WarehouseInventoryResponseDTO(warehouseInventoryRepository.save(warehouseInventory));
    }
}