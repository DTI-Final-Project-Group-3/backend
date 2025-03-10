package com.warehub.warehub.usecase.product.impl;

import com.warehub.warehub.common.utils.ValidationService;
import com.warehub.warehub.entity.Product;
import com.warehub.warehub.entity.ProductCategory;
import com.warehub.warehub.entity.ProductImage;
import com.warehub.warehub.infrastructure.product.dto.ProductImageRequestDTO;
import com.warehub.warehub.infrastructure.product.dto.ProductImageResponseDTO;
import com.warehub.warehub.infrastructure.product.dto.ProductRequestDTO;
import com.warehub.warehub.infrastructure.product.dto.ProductDetailResponseDTO;
import com.warehub.warehub.infrastructure.product.repository.ProductImageRepository;
import com.warehub.warehub.infrastructure.product.repository.ProductRepository;
import com.warehub.warehub.usecase.product.CreateProductUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CreateProductUseCaseImpl implements CreateProductUseCase {

    private final ValidationService validationService;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;

    public CreateProductUseCaseImpl(ValidationService validationService, ProductRepository productRepository, ProductImageRepository productImageRepository) {
        this.validationService = validationService;
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
    }

    @Transactional
    @Override
    public ProductDetailResponseDTO createProduct(ProductRequestDTO req) {

        validationService.validateDuplicateProductName(req.getName());
        ProductCategory productCategory = validationService.validateProductCategoryId(req.getProductCategoryId());
        validationService.validateMaximumSize(req.getImages().size(), 5, "product images");

        Product product = req.toEntity(productCategory);
        productRepository.save(product);

        List<ProductImage> productImages = new ArrayList<ProductImage>();
        for (ProductImageRequestDTO productImage : req.getImages()){
            productImages.add(productImage.toEntity(product));
        }
        productImageRepository.saveAll(productImages);

        List<ProductImageResponseDTO> productImageResponseDTO = productImages.stream().map(ProductImageResponseDTO::new).toList();

        return new ProductDetailResponseDTO(product, productImageResponseDTO);
    }
}
