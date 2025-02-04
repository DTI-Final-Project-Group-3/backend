package com.warehub.warehub.usecase.product_mutation;

import com.warehub.warehub.infrastructure.product_mutation.dto.ApproveProductMutationRequestDTO;
import com.warehub.warehub.infrastructure.product_mutation.dto.ProductMutationRequestDTO;
import com.warehub.warehub.infrastructure.product_mutation.dto.ProductMutationResponseDTO;

public interface UpdateProductMutationUseCase {
    ProductMutationResponseDTO updateProductMutationById(Long productMutationId, ProductMutationRequestDTO req);
    ProductMutationResponseDTO approveManualProductMutation(Long productMutationId, ApproveProductMutationRequestDTO req);
}
