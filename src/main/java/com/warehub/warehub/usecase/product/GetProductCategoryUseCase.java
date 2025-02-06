package com.warehub.warehub.usecase.product;

import com.warehub.warehub.infrastructure.product.dto.ProductCategoryResponseDTO;

import java.util.List;

public interface GetProductCategoryUseCase {
    List<ProductCategoryResponseDTO> getAllProductCategory();
    ProductCategoryResponseDTO getProductCategoryById(Long productCategoryId);
}
