package com.warehub.warehub.usecase.product;

import com.warehub.warehub.common.utils.PaginationInfo;
import com.warehub.warehub.infrastructure.product.dto.PaginatedProductRequestDTO;
import com.warehub.warehub.infrastructure.product.dto.PaginatedProductResponseDTO;
import com.warehub.warehub.infrastructure.product.dto.ProductResponseDTO;

import java.util.List;

public interface GetProductUseCase {
    ProductResponseDTO getProductById(Long productId);
    List<ProductResponseDTO> getAllProduct();
    PaginationInfo<PaginatedProductResponseDTO> getPaginatedProducts(PaginatedProductRequestDTO req);
}
