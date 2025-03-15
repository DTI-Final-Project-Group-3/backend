package com.warehub.warehub.usecase.productMutation.impl;

import com.warehub.warehub.common.enums.MutationConstant;
import com.warehub.warehub.common.exceptions.*;
import com.warehub.warehub.common.utils.CreateProductMutationCode;
import com.warehub.warehub.common.utils.CreateProductMutationLog;
import com.warehub.warehub.common.utils.ValidationService;
import com.warehub.warehub.entity.*;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationRequestDTO;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationResponseDTO;
import com.warehub.warehub.infrastructure.warehouseInventory.repository.WarehouseInventoryRepository;
import com.warehub.warehub.usecase.productMutation.CreateProductMutationUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateProductMutationUseCaseImpl implements CreateProductMutationUseCase {

    private final ValidationService validationService;
    private final WarehouseInventoryRepository warehouseInventoryRepository;
    private final CreateProductMutationLog createProductMutationLog;
    private final CreateProductMutationCode createProductMutationCode;

    public CreateProductMutationUseCaseImpl(ValidationService validationService, WarehouseInventoryRepository warehouseInventoryRepository, CreateProductMutationLog createProductMutationLog, CreateProductMutationCode createProductMutationCode) {
        this.validationService = validationService;
        this.warehouseInventoryRepository = warehouseInventoryRepository;
        this.createProductMutationLog = createProductMutationLog;
        this.createProductMutationCode = createProductMutationCode;
    }

    @Override
    @Transactional
    public ProductMutationResponseDTO createManualMutation(ProductMutationRequestDTO req) {

        // validation request
        Product product = validationService.validateProductId(req.getProductId());
        User requester = validationService.validateUserId(req.getRequesterId());
        Warehouse originWarehouse = validationService.validateWarehouseId(req.getOriginWarehouseId(), "Origin warehouse");
        Warehouse destinationWarehouse = validationService.validateWarehouseId(req.getDestinationWarehouseId(), "Destination warehouse");
        WarehouseInventory originWarehouseInventory = validationService.validateWarehouseInventoryByProductIdAndWarehouseId(req.getProductId(), req.getOriginWarehouseId());

        if (originWarehouseInventory.getQuantity() < req.getQuantity()){
            throw new NegativeQuantityException("Request quantity can't exceed available quantity !");
        }

        // reserve quantity from origin warehouse
        originWarehouseInventory.setQuantity(originWarehouseInventory.getQuantity() - req.getQuantity());
        warehouseInventoryRepository.save(originWarehouseInventory);

        // generate product mutation code
        String productMutationCode = createProductMutationCode.generateProductMutationId();

        // create outbound mutation on origin warehouse (decrease quantity)
       ProductMutation productMutation =  createProductMutationLog
                .createProductMutationRecord(product, req.getQuantity() * -1,
                        req.getRequesterNotes(), requester,
                        originWarehouse, destinationWarehouse,
                        MutationConstant.TYPE_OUTBOUND_MANUAL_MUTATION.getValue(), MutationConstant.STATUS_PENDING.getValue(),
                        null, productMutationCode);

        // create inbound mutation on origin warehouse (increase quantity)
        createProductMutationLog
                .createProductMutationRecord(product, req.getQuantity(),
                        req.getRequesterNotes(), requester,
                        originWarehouse, destinationWarehouse,
                        MutationConstant.TYPE_INBOUND_MANUAL_MUTATION.getValue(), MutationConstant.STATUS_PENDING.getValue(),
                        null, productMutationCode);

        return new ProductMutationResponseDTO(productMutation);
    }

}
