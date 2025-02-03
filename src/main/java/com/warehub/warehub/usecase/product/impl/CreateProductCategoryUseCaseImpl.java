package com.warehub.warehub.usecase.product.impl;

import com.warehub.warehub.common.exceptions.DuplicateProductCategoryException;
import com.warehub.warehub.entity.ProductCategory;
import com.warehub.warehub.infrastructure.product.dto.ProductCategoryRequestDTO;
import com.warehub.warehub.infrastructure.product.dto.ProductCategoryResponseDTO;
import com.warehub.warehub.infrastructure.product.repository.ProductCategoryRepository;
import com.warehub.warehub.usecase.product.CreateProductCategoryUseCase;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CreateProductCategoryUseCaseImpl implements CreateProductCategoryUseCase {
    private final ProductCategoryRepository productCategoryRepository;

    public CreateProductCategoryUseCaseImpl(ProductCategoryRepository productCategoryRepository){
        this.productCategoryRepository = productCategoryRepository;
    }

    @Override
    public ProductCategoryResponseDTO createProductCategory(ProductCategoryRequestDTO req) {
        Optional<ProductCategory> productCategory = productCategoryRepository.findActiveByNameIgnoreCase(req.getName());

        if (productCategory.isPresent()){
            throw new DuplicateProductCategoryException("Product category with name " + req.getName() + " already exist !");
        }
        return new ProductCategoryResponseDTO(productCategoryRepository.save(req.toEntity()));
    }
}
