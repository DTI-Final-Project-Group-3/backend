package com.warehub.warehub.usecase.product.impl;

import com.warehub.warehub.infrastructure.product.dto.ProductRequestDTO;
import com.warehub.warehub.infrastructure.product.dto.ProductResponseDTO;
import com.warehub.warehub.usecase.product.UpdateProductUseCase;
import org.springframework.stereotype.Service;

@Service
public class UpdateProductUseCaseImpl implements UpdateProductUseCase {
    @Override
    public ProductResponseDTO updateProductById(Long productId, ProductRequestDTO req) {
        return null;
    }
}
