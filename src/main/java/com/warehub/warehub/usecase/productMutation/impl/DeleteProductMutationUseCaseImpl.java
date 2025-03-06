package com.warehub.warehub.usecase.productMutation.impl;

import com.warehub.warehub.common.utils.ValidationService;
import com.warehub.warehub.entity.ProductMutation;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationRepository;
import com.warehub.warehub.usecase.productMutation.DeleteProductMutationUseCase;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class DeleteProductMutationUseCaseImpl implements DeleteProductMutationUseCase {

    private final ValidationService validationService;
    private final ProductMutationRepository productMutationRepository;

    public DeleteProductMutationUseCaseImpl(ValidationService validationService, ProductMutationRepository productMutationRepository) {
        this.validationService = validationService;
        this.productMutationRepository = productMutationRepository;
    }

    @Override
    public void deleteProductMutationById(Long productMutationId) {
        ProductMutation productMutation = validationService.validateProductMutationId(productMutationId);

        productMutation.setDeletedAt(OffsetDateTime.now());
        productMutationRepository.save(productMutation);
    }
}