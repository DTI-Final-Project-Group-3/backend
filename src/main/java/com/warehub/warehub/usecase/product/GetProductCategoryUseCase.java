package com.warehub.warehub.usecase.product;

import com.warehub.warehub.common.utils.PaginationInfo;
import com.warehub.warehub.infrastructure.product.dto.PaginatedProductCategoryRequestDTO;
import com.warehub.warehub.infrastructure.product.dto.ProductCategoryResponseDTO;

import java.util.List;

public interface GetProductCategoryUseCase {
    List<ProductCategoryResponseDTO> getAllProductCategory();
    ProductCategoryResponseDTO getProductCategoryById(Long productCategoryId);
    PaginationInfo<ProductCategoryResponseDTO> getPaginatedProductCategory(PaginatedProductCategoryRequestDTO req);
}
