package com.warehub.warehub.usecase.product;

import com.warehub.warehub.common.utils.PaginationInfo;
import com.warehub.warehub.infrastructure.product.dto.*;

import java.util.List;

public interface GetProductUseCase {
    ProductDetailResponseDTO getProductById(Long productId);
    ProductDetailResponseDTO getNearbyProductById(ProductNearbyRequestDTO req);
    List<ProductDetailResponseDTO> getAllProduct();
    PaginationInfo<ProductSummaryResponseDTO> getPaginatedProducts(ProductPaginationRequestDTO req);
    PaginationInfo<ProductSummaryResponseDTO> getPaginatedNearbyProducts(ProductPaginationRequestDTO req);
    List<ProductBasicResponseDTO> getAllProductList();
}
