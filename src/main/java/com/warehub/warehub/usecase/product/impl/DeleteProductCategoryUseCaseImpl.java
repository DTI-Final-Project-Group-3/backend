package com.warehub.warehub.usecase.product.impl;

import com.warehub.warehub.common.enums.MutationConstant;
import com.warehub.warehub.common.exceptions.PendingProductMutationException;
import com.warehub.warehub.common.utils.ValidationService;
import com.warehub.warehub.entity.*;
import com.warehub.warehub.infrastructure.product.repository.ProductCategoryRepository;
import com.warehub.warehub.infrastructure.product.repository.ProductRepository;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationRepository;
import com.warehub.warehub.infrastructure.security.Claims;
import com.warehub.warehub.infrastructure.warehouseInventory.repository.WarehouseInventoryRepository;
import com.warehub.warehub.usecase.product.DeleteProductCategoryUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class DeleteProductCategoryUseCaseImpl implements DeleteProductCategoryUseCase {

    private final ValidationService validationService;
    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductMutationRepository productMutationRepository;
    private final WarehouseInventoryRepository warehouseInventoryRepository;

    public DeleteProductCategoryUseCaseImpl(ValidationService validationService, ProductRepository productRepository, ProductCategoryRepository productCategoryRepository, ProductMutationRepository productMutationRepository, WarehouseInventoryRepository warehouseInventoryRepository) {
        this.validationService = validationService;
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.productMutationRepository = productMutationRepository;
        this.warehouseInventoryRepository = warehouseInventoryRepository;
    }

    @Override
    @Transactional
    public void deleteProductCategoryById(Long productCategoryId) {

        ProductCategory productCategory = validationService.validateProductCategoryId(productCategoryId);
        boolean pendingMutation = productMutationRepository.existPendingMutationByProductCategoryId(MutationConstant.STATUS_PENDING.getValue(), productCategoryId);

        if (pendingMutation){
            throw new PendingProductMutationException("There's still pending product mutation for this category !");
        }

        // soft delete product category
        productCategory.setDeletedAt(OffsetDateTime.now());
        productCategory.setUpdatedAt(OffsetDateTime.now());
        productCategoryRepository.save(productCategory);

        // soft delete product
        List<Product> products = productRepository.findByProductCategoryIdAndDeletedAtIsNull(productCategoryId);
        products.forEach(product -> {
            product.setDeletedAt(OffsetDateTime.now());
            product.setUpdatedAt(OffsetDateTime.now());
            productRepository.save(product);
        });

        // delete inventory and create journal
        List<WarehouseInventory> inventories = warehouseInventoryRepository.findByProductCategoryId(productCategoryId);
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
