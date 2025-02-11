package com.warehub.warehub.usecase.product;

import com.warehub.warehub.infrastructure.product.dto.ProductRequestDTO;
import com.warehub.warehub.infrastructure.product.dto.ProductDetailResponseDTO;

public interface UpdateProductUseCase {
    ProductDetailResponseDTO updateProductById(Long productId, ProductRequestDTO req);
}
