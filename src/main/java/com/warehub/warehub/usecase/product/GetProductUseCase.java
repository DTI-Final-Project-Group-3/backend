package com.warehub.warehub.usecase.product;

import com.warehub.warehub.common.utils.PaginationInfo;
import com.warehub.warehub.infrastructure.product.dto.ProductPaginationRequestDTO;
import com.warehub.warehub.infrastructure.product.dto.ProductSummaryResponseDTO;
import com.warehub.warehub.infrastructure.product.dto.ProductDetailResponseDTO;

import java.util.List;

public interface GetProductUseCase {
    ProductDetailResponseDTO getProductById(Long productId);
    List<ProductDetailResponseDTO> getAllProduct();
    PaginationInfo<ProductSummaryResponseDTO> getPaginatedProducts(ProductPaginationRequestDTO req);
}
