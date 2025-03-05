package com.warehub.warehub.usecase.product.impl;

import com.warehub.warehub.common.utils.ValidationService;
import com.warehub.warehub.infrastructure.product.dto.ProductCategoryRequestDTO;
import com.warehub.warehub.infrastructure.product.dto.ProductCategoryResponseDTO;
import com.warehub.warehub.infrastructure.product.repository.ProductCategoryRepository;
import com.warehub.warehub.usecase.product.CreateProductCategoryUseCase;
import org.springframework.stereotype.Service;

@Service
public class CreateProductCategoryUseCaseImpl implements CreateProductCategoryUseCase {

    private final ValidationService validationService;
    private final ProductCategoryRepository productCategoryRepository;

    public CreateProductCategoryUseCaseImpl(ValidationService validationService, ProductCategoryRepository productCategoryRepository){
        this.validationService = validationService;
        this.productCategoryRepository = productCategoryRepository;
    }

    @Override
    public ProductCategoryResponseDTO createProductCategory(ProductCategoryRequestDTO req) {

        validationService.validateDuplicateProductCategoryName(req.getName());
        return new ProductCategoryResponseDTO(productCategoryRepository.save(req.toEntity()));
    }
}
