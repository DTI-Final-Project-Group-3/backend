package com.warehub.warehub.usecase.product.impl;

import com.warehub.warehub.common.exceptions.MaxListSizeExceededException;
import com.warehub.warehub.common.exceptions.ProductCategoryNotFoundException;
import com.warehub.warehub.common.exceptions.ProductNotFoundException;
import com.warehub.warehub.entity.Product;
import com.warehub.warehub.entity.ProductCategory;
import com.warehub.warehub.entity.ProductImage;
import com.warehub.warehub.infrastructure.product.dto.ProductImageResponseDTO;
import com.warehub.warehub.infrastructure.product.dto.ProductRequestDTO;
import com.warehub.warehub.infrastructure.product.dto.ProductDetailResponseDTO;
import com.warehub.warehub.infrastructure.product.repository.ProductCategoryRepository;
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

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductCategoryRepository productCategoryRepository;

    public UpdateProductUseCaseImpl(ProductRepository productRepository, ProductImageRepository productImageRepository, ProductCategoryRepository productCategoryRepository) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.productCategoryRepository = productCategoryRepository;
    }

    @Override
    @Transactional
    public ProductDetailResponseDTO updateProductById(Long productId, ProductRequestDTO req) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + productId + " not found!"));

        ProductCategory productCategory = productCategoryRepository.findByIdAndDeletedAtIsNull(req.getProductCategoryId())
                .orElseThrow(() -> new ProductCategoryNotFoundException("Product category with ID " + req.getProductCategoryId() + " not found!"));

        Product requestProduct = req.toEntity(productCategory);
        requestProduct.setId(productId);
        productRepository.save(requestProduct);

        if (req.getProductImages().size() > 5) {
            throw new MaxListSizeExceededException("Maximum number of images reached");
        }

        List<ProductImage> existingProductImages = productImageRepository.findByProductIdAndDeletedAtIsNull(productId).stream().toList();
        List<ProductImage> requestProductImages = req.getProductImages().stream()
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
