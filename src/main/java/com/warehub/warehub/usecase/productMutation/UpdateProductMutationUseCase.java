package com.warehub.warehub.usecase.productMutation;

import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationApproveRequestDTO;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationRequestDTO;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationResponseDTO;

public interface UpdateProductMutationUseCase {
    ProductMutationResponseDTO approveManualProductMutation(Long productMutationId, ProductMutationApproveRequestDTO req);
}
