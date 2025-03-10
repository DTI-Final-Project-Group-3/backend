package com.warehub.warehub.usecase.product.impl;

import com.warehub.warehub.common.utils.ValidationService;
import com.warehub.warehub.entity.Product;
import com.warehub.warehub.entity.ProductCategory;
import com.warehub.warehub.entity.ProductImage;
import com.warehub.warehub.infrastructure.product.dto.ProductImageResponseDTO;
import com.warehub.warehub.infrastructure.product.dto.ProductRequestDTO;
import com.warehub.warehub.infrastructure.product.dto.ProductDetailResponseDTO;
import com.warehub.warehub.infrastructure.product.repository.ProductImageRepository;
import com.warehub.warehub.infrastructure.product.repository.ProductRepository;
import com.warehub.warehub.usecase.product.UpdateProductUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UpdateProductUseCaseImpl implements UpdateProductUseCase {

    private final ValidationService validationService;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;

    public UpdateProductUseCaseImpl(ValidationService validationService, ProductRepository productRepository, ProductImageRepository productImageRepository) {
        this.validationService = validationService;
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
    }

    @Override
    @Transactional
    public ProductDetailResponseDTO updateProductById(Long productId, ProductRequestDTO req) {
        validationService.validateProductId(productId);
        ProductCategory productCategory = validationService.validateProductCategoryId(req.getProductCategoryId());
        validationService.validateMaximumSize(req.getImages().size(), 5, "images");
        Product product = validationService.validateProductId(productId);

        // update product entity
        Product requestProduct = req.toEntity(productCategory);
        requestProduct.setId(productId);
        requestProduct.setUpdatedAt(OffsetDateTime.now());
        requestProduct.setCreatedAt(product.getCreatedAt());
        productRepository.save(requestProduct);

        List<ProductImage> existingProductImages = productImageRepository.findByProductIdAndDeletedAtIsNull(productId).stream().toList();
        List<ProductImage> requestProductImages = req.getImages().stream()
                .map(productImageRequestDTO -> productImageRequestDTO.toEntity(requestProduct)).toList();
        List<ProductImage> updatedProductImages = new ArrayList<>();
        List<ProductImageResponseDTO> productImageResponseDTOS = new ArrayList<>();

        List<Integer> requestImageOrderNumbers = requestProductImages.stream()
                .map(ProductImage::getPosition)
                .toList();
        existingProductImages.stream()
                .filter(existingImage -> !requestImageOrderNumbers.contains(existingImage.getPosition()))
                .forEach(imageToDelete -> {
                    imageToDelete.setDeletedAt(OffsetDateTime.now());
                    productImageRepository.save(imageToDelete);
                });

        for (ProductImage requestProductImage : requestProductImages) {
            ProductImage newProductImage = new ProductImage();
            existingProductImages.stream()
                    .filter(existingProductImage -> existingProductImage.getPosition().equals(requestProductImage.getPosition()))
                    .findFirst()
                    .ifPresent(existingProductImage -> {
                        newProductImage.setId(existingProductImage.getId());
                        newProductImage.setCreatedAt(existingProductImage.getCreatedAt());
                    });

            newProductImage.setProduct(requestProduct);
            newProductImage.setUrl(requestProductImage.getUrl());
            newProductImage.setPosition(requestProductImage.getPosition());
            newProductImage.setUpdatedAt(OffsetDateTime.now());

            if(newProductImage.getId() == null){
                newProductImage.setCreatedAt(OffsetDateTime.now());
            }

            updatedProductImages.add(newProductImage);
        }
        productImageRepository.saveAll(updatedProductImages);
        updatedProductImages.forEach(updatedProductImage -> productImageResponseDTOS.add(new ProductImageResponseDTO(updatedProductImage)));

        return new ProductDetailResponseDTO(requestProduct, productImageResponseDTOS);
    }
}
