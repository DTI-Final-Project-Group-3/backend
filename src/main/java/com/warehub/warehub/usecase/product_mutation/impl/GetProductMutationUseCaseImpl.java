package com.warehub.warehub.usecase.product_mutation.impl;

import com.warehub.warehub.common.exceptions.ProductMutationNotFoundException;
import com.warehub.warehub.entity.ProductMutation;
import com.warehub.warehub.infrastructure.product_mutation.dto.ProductMutationResponseDTO;
import com.warehub.warehub.infrastructure.product_mutation.repository.ProductMutationRepository;
import com.warehub.warehub.usecase.product_mutation.GetProductMutationUseCase;
import org.springframework.stereotype.Service;

@Service
public class GetProductMutationUseCaseImpl implements GetProductMutationUseCase {

    private final ProductMutationRepository productMutationRepository;

    public GetProductMutationUseCaseImpl(ProductMutationRepository productMutationRepository) {
        this.productMutationRepository = productMutationRepository;
    }

    @Override
    public ProductMutationResponseDTO getProductMutationById(Long productMutationId) {

        ProductMutation productMutation = productMutationRepository.findByIdAndDeletedAtIsNull(productMutationId)
                .orElseThrow(()-> new ProductMutationNotFoundException("Product mutation with Id" + productMutationId + " not found !"));

        return new ProductMutationResponseDTO(productMutation);
    }
}
