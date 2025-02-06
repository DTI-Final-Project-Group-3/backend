package com.warehub.warehub.usecase.product;

import com.warehub.warehub.infrastructure.product.dto.ProductRequestDTO;
import com.warehub.warehub.infrastructure.product.dto.ProductResponseDTO;

public interface UpdateProductUseCase {
    ProductResponseDTO updateProductById(Long productId, ProductRequestDTO req);
}
