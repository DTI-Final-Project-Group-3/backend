package com.warehub.warehub.usecase.product.impl;

import com.warehub.warehub.common.exceptions.ProductCategoryNotFoundException;
import com.warehub.warehub.entity.ProductCategory;
import com.warehub.warehub.infrastructure.product.dto.ProductCategoryRequestDTO;
import com.warehub.warehub.infrastructure.product.dto.ProductCategoryResponseDTO;
import com.warehub.warehub.infrastructure.product.repository.ProductCategoryRepository;
import com.warehub.warehub.usecase.product.UpdateProductCategoryUseCase;
import org.springframework.stereotype.Service;

@Service
public class UpdateProductCategoryUseCaseImpl implements UpdateProductCategoryUseCase {

    private final ProductCategoryRepository productCategoryRepository;

    public UpdateProductCategoryUseCaseImpl(ProductCategoryRepository productCategoryRepository) {
        this.productCategoryRepository = productCategoryRepository;
    }

    @Override
    public ProductCategoryResponseDTO updateProductCategoryById(Long productCategoryId, ProductCategoryRequestDTO req) {
        ProductCategory productCategory = productCategoryRepository.findByIdAndDeletedAtIsNull(productCategoryId)
                .orElseThrow(()-> new ProductCategoryNotFoundException("Product category with ID "+ productCategoryId + " not found !"));

        ProductCategory updatedProductCategory = req.toEntity();
        updatedProductCategory.setId(productCategoryId);

        return new ProductCategoryResponseDTO(productCategoryRepository.save(updatedProductCategory));
    }
}
