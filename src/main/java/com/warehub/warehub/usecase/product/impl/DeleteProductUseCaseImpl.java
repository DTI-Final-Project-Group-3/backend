package com.warehub.warehub.usecase.product.impl;

import com.warehub.warehub.common.exceptions.ProductNotFoundException;
import com.warehub.warehub.entity.Product;
import com.warehub.warehub.infrastructure.product.repository.ProductRepository;
import com.warehub.warehub.usecase.product.DeleteProductUseCase;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class DeleteProductUseCaseImpl implements DeleteProductUseCase {
    private final ProductRepository productRepository;

    public DeleteProductUseCaseImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void deleteProductById(Long productId) {
        Product product = productRepository.findActiveById(productId)
                .orElseThrow(()-> new ProductNotFoundException("Product with ID " + productId + " not found !"));

        product.setDeletedAt(OffsetDateTime.now());
        productRepository.save(product);
    }
}
