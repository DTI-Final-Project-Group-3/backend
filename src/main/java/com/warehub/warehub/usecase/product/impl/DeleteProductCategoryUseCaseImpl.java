package com.warehub.warehub.usecase.product.impl;

import com.warehub.warehub.common.enums.MutationConstant;
import com.warehub.warehub.common.exceptions.PendingProductMutationException;
import com.warehub.warehub.common.utils.ValidationService;
import com.warehub.warehub.entity.Product;
import com.warehub.warehub.entity.ProductCategory;
import com.warehub.warehub.entity.WarehouseInventory;
import com.warehub.warehub.infrastructure.product.repository.ProductCategoryRepository;
import com.warehub.warehub.infrastructure.product.repository.ProductRepository;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationRepository;
import com.warehub.warehub.infrastructure.warehouseInventory.repository.WarehouseInventoryRepository;
import com.warehub.warehub.usecase.product.DeleteProductCategoryUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class DeleteProductCategoryUseCaseImpl implements DeleteProductCategoryUseCase {

    private final ValidationService validationService;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductMutationRepository productMutationRepository;
    private final WarehouseInventoryRepository warehouseInventoryRepository;

    public DeleteProductCategoryUseCaseImpl(ValidationService validationService, ProductCategoryRepository productCategoryRepository, ProductMutationRepository productMutationRepository, WarehouseInventoryRepository warehouseInventoryRepository) {
        this.validationService = validationService;
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

        productCategory.setDeletedAt(OffsetDateTime.now());
        productCategoryRepository.save(productCategory);

        List<WarehouseInventory> inventories = warehouseInventoryRepository.findByProductCategoryId(productCategoryId);

        inventories.forEach(inventory -> {
            inventory.setDeletedAt(OffsetDateTime.now());
            warehouseInventoryRepository.save(inventory);
        });
    }
}
