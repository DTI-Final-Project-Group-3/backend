package com.warehub.warehub.usecase.productMutation;

import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationRequestDTO;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationResponseDTO;

public interface CreateProductMutationUseCase {
    ProductMutationResponseDTO createManualMutation(ProductMutationRequestDTO req);
    ProductMutationResponseDTO createAutoMutation(ProductMutationRequestDTO req);
}