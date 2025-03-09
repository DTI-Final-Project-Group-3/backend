package com.warehub.warehub.usecase.product.impl;

import com.warehub.warehub.common.utils.PaginationInfo;
import com.warehub.warehub.common.utils.ValidationService;
import com.warehub.warehub.entity.ProductCategory;
import com.warehub.warehub.infrastructure.product.dto.PaginatedProductCategoryRequestDTO;
import com.warehub.warehub.infrastructure.product.dto.ProductCategoryResponseDTO;
import com.warehub.warehub.infrastructure.product.repository.ProductCategoryRepository;
import com.warehub.warehub.usecase.product.GetProductCategoryUseCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetProductCategoryUseCaseImpl implements GetProductCategoryUseCase {

    private final ValidationService validationService;
    private final ProductCategoryRepository productCategoryRepository;

    public GetProductCategoryUseCaseImpl(ValidationService validationService, ProductCategoryRepository productCategoryRepository) {
        this.validationService = validationService;
        this.productCategoryRepository = productCategoryRepository;
    }

    @Override
    public List<ProductCategoryResponseDTO> getAllProductCategory() {

        List<ProductCategory> productCategories = productCategoryRepository.findAllByDeletedAtIsNull();

        return productCategories.stream().map(ProductCategoryResponseDTO::new).toList();
    }

    @Override
    public ProductCategoryResponseDTO getProductCategoryById(Long productCategoryId) {
        ProductCategory productCategory = validationService.validateProductCategoryId(productCategoryId);

        return new ProductCategoryResponseDTO(productCategory);
    }

    @Override
    public PaginationInfo<ProductCategoryResponseDTO> getPaginatedProductCategory(PaginatedProductCategoryRequestDTO req) {
        PageRequest pageRequest=  PageRequest.of(req.getPage(), req.getLimit());

        Page<ProductCategoryResponseDTO> responseDTOS = productCategoryRepository.findPaginatedProductCategories(pageRequest);

        return new PaginationInfo<>(responseDTOS, responseDTOS.getContent());
    }
}
