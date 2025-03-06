package com.warehub.warehub.usecase.product.impl;

import com.warehub.warehub.common.utils.ValidationService;
import com.warehub.warehub.entity.Product;
import com.warehub.warehub.entity.ProductCategory;
import com.warehub.warehub.infrastructure.product.repository.ProductCategoryRepository;
import com.warehub.warehub.infrastructure.product.repository.ProductRepository;
import com.warehub.warehub.usecase.product.DeleteProductCategoryUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class DeleteProductCategoryUseCaseImpl implements DeleteProductCategoryUseCase {

    private final ValidationService validationService;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductRepository productRepository;

    public DeleteProductCategoryUseCaseImpl(ValidationService validationService, ProductCategoryRepository productCategoryRepository, ProductRepository productRepository) {
        this.validationService = validationService;
        this.productCategoryRepository = productCategoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public void deleteProductCategoryById(Long productCategoryId) {

        ProductCategory productCategory = validationService.validateProductCategoryId(productCategoryId);

        productCategory.setDeletedAt(OffsetDateTime.now());
        productCategoryRepository.save(productCategory);

        List<Product> products = productRepository.findByProductCategoryIdAndDeletedAtIsNull(productCategoryId);

        products.forEach(product -> {
            product.setDeletedAt(OffsetDateTime.now());
            productRepository.save(product);
        });
    }
}
