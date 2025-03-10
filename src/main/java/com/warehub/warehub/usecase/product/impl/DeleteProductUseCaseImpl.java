package com.warehub.warehub.usecase.product.impl;

import com.warehub.warehub.common.enums.MutationConstant;
import com.warehub.warehub.common.exceptions.PendingProductMutationException;
import com.warehub.warehub.common.utils.ValidationService;
import com.warehub.warehub.entity.*;
import com.warehub.warehub.infrastructure.product.repository.ProductRepository;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationRepository;
import com.warehub.warehub.infrastructure.security.Claims;
import com.warehub.warehub.infrastructure.warehouseInventory.repository.WarehouseInventoryRepository;
import com.warehub.warehub.usecase.product.DeleteProductUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class DeleteProductUseCaseImpl implements DeleteProductUseCase {

    private final ValidationService validationService;
    private final ProductRepository productRepository;
    private final ProductMutationRepository productMutationRepository;
    private final WarehouseInventoryRepository warehouseInventoryRepository;

    public DeleteProductUseCaseImpl(ValidationService validationService, ProductRepository productRepository, ProductMutationRepository productMutationRepository, WarehouseInventoryRepository warehouseInventoryRepository) {
        this.validationService = validationService;
        this.productRepository = productRepository;
        this.productMutationRepository = productMutationRepository;
        this.warehouseInventoryRepository = warehouseInventoryRepository;
    }

    @Override
    @Transactional
    public void deleteProductById(Long productId) {

        // check pending mutation
        boolean pendingMutation = productMutationRepository.existPendingMutationByProductId(MutationConstant.STATUS_PENDING.getValue(), productId);
        if (pendingMutation){
            throw new PendingProductMutationException("There's still pending mutation for this product !");
        }

        // delete product
        Product product = validationService.validateProductId(productId);
        product.setDeletedAt(OffsetDateTime.now());
        product.setUpdatedAt(OffsetDateTime.now());
        productRepository.save(product);

        // delete inventory and create journal
        List<WarehouseInventory> inventories = warehouseInventoryRepository.findByProductIdAndDeletedAtIsNull(productId);
        inventories.forEach(inventory -> {

            // delete inventory
            inventory.setDeletedAt(OffsetDateTime.now());
            warehouseInventoryRepository.save(inventory);

            ProductMutationType mutationType = validationService.validateProductMutationTypeId(MutationConstant.TYPE_DELETE_INVENTORY.getValue());
            ProductMutationStatus mutationStatus = validationService.validateProductMutationStatusId(MutationConstant.STATUS_COMPLETED.getValue());
            User requester = validationService.validateUserId(Claims.getUserIdFromJwt());

            // create mutation
            ProductMutation productMutation = new ProductMutation();
            productMutation.setProduct(inventory.getProduct());
            productMutation.setProductMutationType(mutationType);
            productMutation.setProductMutationStatus(mutationStatus);
            productMutation.setQuantity(inventory.getQuantity() * -1);
            productMutation.setRequester(requester);
            productMutation.setRequesterNotes("Product category for this product is deleted by Super Admin");
            productMutation.setDestinationWarehouse(inventory.getWarehouse());
            productMutationRepository.save(productMutation);
        });


    }
}
