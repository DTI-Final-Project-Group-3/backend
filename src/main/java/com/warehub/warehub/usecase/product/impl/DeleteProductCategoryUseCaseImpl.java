package com.warehub.warehub.usecase.product.impl;

import com.warehub.warehub.common.exceptions.ProductCategoryNotFoundException;
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

    private final ProductCategoryRepository productCategoryRepository;
    private final ProductRepository productRepository;

    public DeleteProductCategoryUseCaseImpl(ProductCategoryRepository productCategoryRepository, ProductRepository productRepository) {
        this.productCategoryRepository = productCategoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public void deleteProductCategoryById(Long productCategoryId) {

        ProductCategory productCategory = productCategoryRepository.findByIdAndDeletedAtIsNull(productCategoryId)
                .orElseThrow(()-> new ProductCategoryNotFoundException("Product category with ID "+ productCategoryId + " not found !"));

        productCategory.setDeletedAt(OffsetDateTime.now());

        List<Product> products = productRepository.findByProductCategoryIdAndDeletedAtIsNull(productCategoryId);

        products.forEach(product -> {
            product.setDeletedAt(OffsetDateTime.now());
            productRepository.save(product);
        });
    }
}
