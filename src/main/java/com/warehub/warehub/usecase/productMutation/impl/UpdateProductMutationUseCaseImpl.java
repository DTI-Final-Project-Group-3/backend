package com.warehub.warehub.usecase.productMutation.impl;

import com.warehub.warehub.common.enums.MutationConstant;
import com.warehub.warehub.common.utils.CreateProductMutationLog;
import com.warehub.warehub.common.utils.ValidationService;
import com.warehub.warehub.entity.*;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationProcessRequestDTO;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationResponseDTO;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationRepository;
import com.warehub.warehub.infrastructure.warehouseInventory.repository.WarehouseInventoryRepository;
import com.warehub.warehub.usecase.productMutation.UpdateProductMutationUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class UpdateProductMutationUseCaseImpl implements UpdateProductMutationUseCase {

    private final ValidationService validationService;
    private final WarehouseInventoryRepository warehouseInventoryRepository;
    private final CreateProductMutationLog createProductMutationLog;
    private final ProductMutationRepository productMutationRepository;

    public UpdateProductMutationUseCaseImpl(ValidationService validationService, WarehouseInventoryRepository warehouseInventoryRepository, CreateProductMutationLog createProductMutationLog, ProductMutationRepository productMutationRepository) {
        this.validationService = validationService;
        this.warehouseInventoryRepository = warehouseInventoryRepository;
        this.createProductMutationLog = createProductMutationLog;
        this.productMutationRepository = productMutationRepository;
    }

    @Override
    @Transactional
    public ProductMutationResponseDTO approveManualProductMutation(Long productMutationId, ProductMutationProcessRequestDTO req) {

        // validate request
        User reviewer = validationService.validateUserId(req.getUserId());
        ProductMutation productMutation = validationService.validateProductMutationId(productMutationId);
        WarehouseInventory destinationWarehouseInventory = validationService.validateWarehouseInventoryByProductIdAndWarehouseId(productMutation.getProduct().getId(), productMutation.getDestinationWarehouse().getId());

        // every request have negative value
        int changeQuantity = productMutation.getQuantity() * -1;

        // increase quantity on destination warehouse
        destinationWarehouseInventory.setQuantity(destinationWarehouseInventory.getQuantity() + changeQuantity);
        warehouseInventoryRepository.save(destinationWarehouseInventory);

        // find existing productMutation and update it
        List<ProductMutation> prevLogs = productMutationRepository.findByProductMutationCodeAndDeletedAtIsNull(productMutation.getProductMutationCode());
        for (ProductMutation mutation : prevLogs){
            mutation.setReviewedAt(OffsetDateTime.now());
            productMutationRepository.save(mutation);
        }

        // Create new outbound mutation on origin warehouse (decrease quantity)
        createProductMutationLog
                .createProductMutationRecord(productMutation.getProduct(), changeQuantity * -1,
                        productMutation.getRequesterNotes(), productMutation.getRequester(),
                        productMutation.getOriginWarehouse(), productMutation.getDestinationWarehouse(),
                        MutationConstant.TYPE_OUTBOUND_MANUAL_MUTATION.getValue(), MutationConstant.STATUS_COMPLETED.getValue(), null,
                        req.getNotes(), reviewer, OffsetDateTime.now(), productMutation.getProductMutationCode());

        // Create new inbound mutation on destination warehouse (increase quantity)
        createProductMutationLog
                .createProductMutationRecord(productMutation.getProduct(), changeQuantity,
                        productMutation.getRequesterNotes(), productMutation.getRequester(),
                        productMutation.getOriginWarehouse(), productMutation.getDestinationWarehouse(),
                        MutationConstant.TYPE_INBOUND_MANUAL_MUTATION.getValue(), MutationConstant.STATUS_COMPLETED.getValue(), null,
                        req.getNotes(), reviewer, OffsetDateTime.now(), productMutation.getProductMutationCode());

        return new ProductMutationResponseDTO(productMutation);
    }

    @Override
    @Transactional
    public ProductMutationResponseDTO declineManualProductMutation(Long productMutationId, ProductMutationProcessRequestDTO req) {

        // validate request
        User reviewer = validationService.validateUserId(req.getUserId());
        ProductMutation productMutation = validationService.validateProductMutationId(productMutationId);
        WarehouseInventory originWarehouseInventory = validationService.validateWarehouseInventoryByProductIdAndWarehouseId(productMutation.getProduct().getId(), productMutation.getOriginWarehouse().getId());

        // every request have negative value
        int changeQuantity = productMutation.getQuantity() * -1;

        // roll back quantity from origin warehouse
        originWarehouseInventory.setQuantity(originWarehouseInventory.getQuantity() + changeQuantity);
        warehouseInventoryRepository.save(originWarehouseInventory);

        // find existing productMutation and update it
        List<ProductMutation> prevLogs = productMutationRepository.findByProductMutationCodeAndDeletedAtIsNull(productMutation.getProductMutationCode());
        for (ProductMutation mutation : prevLogs){
            mutation.setReviewedAt(OffsetDateTime.now());
            productMutationRepository.save(mutation);
        }

        // Create new inbound mutation on origin warehouse (increase quantity)
        createProductMutationLog
                .createProductMutationRecord(productMutation.getProduct(), changeQuantity,
                        productMutation.getRequesterNotes(), productMutation.getRequester(),
                        productMutation.getOriginWarehouse(), productMutation.getDestinationWarehouse(),
                        MutationConstant.TYPE_INBOUND_MANUAL_MUTATION.getValue(), MutationConstant.STATUS_COMPLETED.getValue(), null,
                        req.getNotes(), reviewer, OffsetDateTime.now(), productMutation.getProductMutationCode());

        // Create new outbound mutation on destination warehouse (decrease quantity)
        createProductMutationLog
                .createProductMutationRecord(productMutation.getProduct(), changeQuantity * -1,
                        productMutation.getRequesterNotes(), productMutation.getRequester(),
                        productMutation.getOriginWarehouse(), productMutation.getDestinationWarehouse(),
                        MutationConstant.TYPE_INBOUND_MANUAL_MUTATION.getValue(), MutationConstant.STATUS_COMPLETED.getValue(), null,
                        req.getNotes(), reviewer, OffsetDateTime.now(), productMutation.getProductMutationCode());

        return new ProductMutationResponseDTO(productMutation);
    }
}
