package com.warehub.warehub.usecase.productMutation;

import com.warehub.warehub.common.utils.PaginationInfo;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationPaginationRequestDTO;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationResponseDTO;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationDetailResponseDTO;


public interface GetProductMutationUseCase {
    ProductMutationResponseDTO getProductMutationById(Long productMutationId);
    PaginationInfo<ProductMutationDetailResponseDTO> getPaginatedProductMutationByWarehouseId(ProductMutationPaginationRequestDTO req);
}
