package com.warehub.warehub.usecase.productMutation;

import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationResponseDTO;

public interface GetProductMutationUseCase {
    ProductMutationResponseDTO getProductMutationById(Long productMutationId);
}
