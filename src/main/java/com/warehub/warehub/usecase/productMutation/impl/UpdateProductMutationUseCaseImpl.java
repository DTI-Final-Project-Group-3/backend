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
import java.util.List;

@Service
public class UpdateProductMutationUseCaseImpl implements UpdateProductMutationUseCase {

    private final ValidationService validationService;
    private final WarehouseInventoryRepository warehouseInventoryRepository;
    private final ProductMutationRepository productMutationRepository;

    public UpdateProductMutationUseCaseImpl(ValidationService validationService, WarehouseInventoryRepository warehouseInventoryRepository, ProductMutationRepository productMutationRepository) {
        this.validationService = validationService;
        this.warehouseInventoryRepository = warehouseInventoryRepository;
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

        ProductMutationStatus statusAccept = validationService.validateProductMutationStatusId(MutationConstant.STATUS_COMPLETED.getValue());

        // find existing productMutation and update it
        List<ProductMutation> prevLogs = productMutationRepository.findByProductMutationCodeAndDeletedAtIsNull(productMutation.getProductMutationCode());
        for (ProductMutation mutation : prevLogs){
            mutation.setReviewer(reviewer);
            mutation.setReviewedAt(OffsetDateTime.now());
            mutation.setProductMutationStatus(statusAccept);
            productMutationRepository.save(mutation);
        }

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

        ProductMutationStatus statusDecline = validationService.validateProductMutationStatusId(MutationConstant.STATUS_DECLINED.getValue());

        // find existing productMutation and update it
        List<ProductMutation> prevLogs = productMutationRepository.findByProductMutationCodeAndDeletedAtIsNull(productMutation.getProductMutationCode());
        for (ProductMutation mutation : prevLogs){
            mutation.setReviewer(reviewer);
            mutation.setReviewedAt(OffsetDateTime.now());
            mutation.setProductMutationStatus(statusDecline);
            productMutationRepository.save(mutation);
        }

        return new ProductMutationResponseDTO(productMutation);
    }
}
