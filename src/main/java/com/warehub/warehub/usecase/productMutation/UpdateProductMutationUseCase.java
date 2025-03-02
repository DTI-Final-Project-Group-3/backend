package com.warehub.warehub.usecase.productMutation;

import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationProcessRequestDTO;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationResponseDTO;

public interface UpdateProductMutationUseCase {
    ProductMutationResponseDTO approveManualProductMutation(Long productMutationId, ProductMutationProcessRequestDTO req);
    ProductMutationResponseDTO declineManualProductMutation(Long productMutationId, ProductMutationProcessRequestDTO req);

}