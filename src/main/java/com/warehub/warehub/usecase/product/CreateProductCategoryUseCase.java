package com.warehub.warehub.usecase.product;

import com.warehub.warehub.infrastructure.product.dto.ProductCategoryRequestDTO;
import com.warehub.warehub.infrastructure.product.dto.ProductCategoryResponseDTO;

public interface CreateProductCategoryUseCase {
    ProductCategoryResponseDTO createProductCategory(ProductCategoryRequestDTO req);
}
