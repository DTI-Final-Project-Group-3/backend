package com.warehub.warehub.usecase.productMutation;

import com.warehub.warehub.infrastructure.productMutation.dto.ApproveProductMutationRequestDTO;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationRequestDTO;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationResponseDTO;

public interface UpdateProductMutationUseCase {
    ProductMutationResponseDTO updateProductMutationById(Long productMutationId, ProductMutationRequestDTO req);
    ProductMutationResponseDTO approveManualProductMutation(Long productMutationId, ApproveProductMutationRequestDTO req);
}
