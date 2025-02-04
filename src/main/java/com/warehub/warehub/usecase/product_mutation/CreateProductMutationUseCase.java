package com.warehub.warehub.usecase.product_mutation;

import com.warehub.warehub.infrastructure.product_mutation.dto.ProductMutationRequestDTO;
import com.warehub.warehub.infrastructure.product_mutation.dto.ProductMutationResponseDTO;

public interface CreateProductMutationUseCase {
    ProductMutationResponseDTO createManualMutation(ProductMutationRequestDTO req);
    ProductMutationResponseDTO createAutoMutation(ProductMutationRequestDTO req);
}
