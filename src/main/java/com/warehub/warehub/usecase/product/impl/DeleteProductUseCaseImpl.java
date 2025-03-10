package com.warehub.warehub.usecase.product.impl;

import com.warehub.warehub.common.utils.ValidationService;
import com.warehub.warehub.entity.Product;
import com.warehub.warehub.infrastructure.product.repository.ProductRepository;
import com.warehub.warehub.usecase.product.DeleteProductUseCase;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class DeleteProductUseCaseImpl implements DeleteProductUseCase {

    private final ValidationService validationService;
    private final ProductRepository productRepository;

    public DeleteProductUseCaseImpl(ValidationService validationService, ProductRepository productRepository) {
        this.validationService = validationService;
        this.productRepository = productRepository;
    }

    @Override
    public void deleteProductById(Long productId) {
        Product product = validationService.validateProductId(productId);

        product.setDeletedAt(OffsetDateTime.now());
        productRepository.save(product);
    }
}
