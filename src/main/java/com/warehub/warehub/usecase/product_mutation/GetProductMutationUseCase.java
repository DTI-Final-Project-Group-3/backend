package com.warehub.warehub.usecase.product_mutation;

import com.warehub.warehub.infrastructure.product_mutation.dto.ProductMutationResponseDTO;

public interface GetProductMutationUseCase {
    ProductMutationResponseDTO getProductMutationById(Long productMutationId);
}
