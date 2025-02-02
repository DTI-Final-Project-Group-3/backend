package com.warehub.warehub.usecase.product;

import com.warehub.warehub.common.utils.PaginationInfo;
import com.warehub.warehub.infrastructure.product.dto.ProductResponseDTO;

import java.util.List;

public interface GetProductUseCase {
    ProductResponseDTO getProductById(Long productId);
    PaginationInfo<ProductResponseDTO> getPaginatedProducts(int page, int limit, Long productCategoryId, List<Integer> sortedWarehouse);
}
