package com.warehub.warehub.usecase.product.impl;

import com.warehub.warehub.common.utils.ValidationService;
import com.warehub.warehub.entity.ProductCategory;
import com.warehub.warehub.infrastructure.product.dto.ProductCategoryRequestDTO;
import com.warehub.warehub.infrastructure.product.dto.ProductCategoryResponseDTO;
import com.warehub.warehub.infrastructure.product.repository.ProductCategoryRepository;
import com.warehub.warehub.usecase.product.UpdateProductCategoryUseCase;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class UpdateProductCategoryUseCaseImpl implements UpdateProductCategoryUseCase {

    private final ValidationService validationService;
    private final ProductCategoryRepository productCategoryRepository;

    public UpdateProductCategoryUseCaseImpl(ValidationService validationService, ProductCategoryRepository productCategoryRepository) {
        this.validationService = validationService;
        this.productCategoryRepository = productCategoryRepository;
    }

    @Override
    public ProductCategoryResponseDTO updateProductCategoryById(Long productCategoryId, ProductCategoryRequestDTO req) {
        ProductCategory productCategory = validationService.validateProductCategoryId(productCategoryId);
        productCategory.setName(req.getName());
        productCategory.setUpdatedAt(OffsetDateTime.now());

        return new ProductCategoryResponseDTO(productCategoryRepository.save(productCategory));
    }
}
