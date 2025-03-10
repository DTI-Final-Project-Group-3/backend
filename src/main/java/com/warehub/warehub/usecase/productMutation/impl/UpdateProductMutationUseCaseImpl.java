package com.warehub.warehub.usecase.productMutation.impl;

import com.warehub.warehub.common.enums.MutationConstant;
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

@Service
public class UpdateProductMutationUseCaseImpl implements UpdateProductMutationUseCase {

    private final ValidationService validationService;
    private final ProductMutationRepository productMutationRepository;
    private final WarehouseInventoryRepository warehouseInventoryRepository;

    public UpdateProductMutationUseCaseImpl(ValidationService validationService, ProductMutationRepository productMutationRepository, WarehouseInventoryRepository warehouseInventoryRepository) {
        this.validationService = validationService;
        this.productMutationRepository = productMutationRepository;
        this.warehouseInventoryRepository = warehouseInventoryRepository;
    }

    @Override
    @Transactional
    public ProductMutationResponseDTO approveManualProductMutation(Long productMutationId, ProductMutationProcessRequestDTO req) {

        // validate request
        User reviewer = validationService.validateUserId(req.getUserId());
        ProductMutationStatus productMutationStatus = validationService.validateProductMutationStatusId(MutationConstant.STATUS_COMPLETED.getValue());
        ProductMutation productMutation = validationService.validateProductMutationId(productMutationId);
        WarehouseInventory destinationWarehouseInventory = validationService.validateWarehouseInventoryByProductIdAndWarehouseId(productMutation.getProduct().getId(), productMutation.getDestinationWarehouse().getId());

        // increase quantity from destination warehouse
        destinationWarehouseInventory.setQuantity(destinationWarehouseInventory.getQuantity() + productMutation.getQuantity());
        warehouseInventoryRepository.save(destinationWarehouseInventory);

        // update product mutation to completed
        productMutation.setReviewer(reviewer);
        productMutation.setReviewerNotes(req.getNotes());
        productMutation.setReviewedAt(OffsetDateTime.now());
        productMutation.setProductMutationStatus(productMutationStatus);
        productMutation.setUpdatedAt(OffsetDateTime.now());

        return new ProductMutationResponseDTO(productMutationRepository.save(productMutation));
    }

    @Override
    @Transactional
    public ProductMutationResponseDTO declineManualProductMutation(Long productMutationId, ProductMutationProcessRequestDTO req) {

        // validate request
        User reviewer = validationService.validateUserId(req.getUserId());
        ProductMutationStatus productMutationStatus = validationService.validateProductMutationStatusId(MutationConstant.STATUS_DECLINED.getValue());
        ProductMutation productMutation = validationService.validateProductMutationId(productMutationId);
        WarehouseInventory originWarehouseInventory = validationService.validateWarehouseInventoryByProductIdAndWarehouseId(productMutation.getProduct().getId(), productMutation.getOriginWarehouse().getId());

        // roll back quantity from origin warehouse
        originWarehouseInventory.setQuantity(originWarehouseInventory.getQuantity() + productMutation.getQuantity());
        warehouseInventoryRepository.save(originWarehouseInventory);

        // update product mutation to completed
        productMutation.setReviewer(reviewer);
        productMutation.setReviewerNotes(req.getNotes());
        productMutation.setReviewedAt(OffsetDateTime.now());
        productMutation.setProductMutationStatus(productMutationStatus);
        productMutation.setUpdatedAt(OffsetDateTime.now());

        return new ProductMutationResponseDTO(productMutationRepository.save(productMutation));
    }
}
