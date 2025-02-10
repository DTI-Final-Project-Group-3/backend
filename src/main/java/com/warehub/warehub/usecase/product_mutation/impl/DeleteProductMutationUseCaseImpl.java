package com.warehub.warehub.usecase.product_mutation.impl;

import com.warehub.warehub.common.exceptions.ProductMutationNotFoundException;
import com.warehub.warehub.entity.ProductMutation;
import com.warehub.warehub.infrastructure.product_mutation.repository.ProductMutationRepository;
import com.warehub.warehub.usecase.product_mutation.DeleteProductMutationUseCase;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class DeleteProductMutationUseCaseImpl implements DeleteProductMutationUseCase {

    private final ProductMutationRepository productMutationRepository;

    public DeleteProductMutationUseCaseImpl(ProductMutationRepository productMutationRepository) {
        this.productMutationRepository = productMutationRepository;
    }

    @Override
    public void deleteProductMutationById(Long productMutationId) {
        ProductMutation productMutation = productMutationRepository.findByIdAndDeletedAtIsNull(productMutationId)
                .orElseThrow(()-> new ProductMutationNotFoundException("Product mutation with ID "+ productMutationId + " not found !"));

        productMutation.setDeletedAt(OffsetDateTime.now());
        productMutationRepository.save(productMutation);
    }
}
