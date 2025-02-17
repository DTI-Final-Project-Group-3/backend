package com.warehub.warehub.usecase.product.impl;

import com.warehub.warehub.common.exceptions.DuplicateProductException;
import com.warehub.warehub.common.exceptions.MaxListSizeExceededException;
import com.warehub.warehub.common.exceptions.ProductCategoryNotFoundException;
import com.warehub.warehub.entity.Product;
import com.warehub.warehub.entity.ProductCategory;
import com.warehub.warehub.entity.ProductImage;
import com.warehub.warehub.infrastructure.product.dto.ProductImageRequestDTO;
import com.warehub.warehub.infrastructure.product.dto.ProductImageResponseDTO;
import com.warehub.warehub.infrastructure.product.dto.ProductRequestDTO;
import com.warehub.warehub.infrastructure.product.dto.ProductDetailResponseDTO;
import com.warehub.warehub.infrastructure.product.repository.ProductCategoryRepository;
import com.warehub.warehub.infrastructure.product.repository.ProductImageRepository;
import com.warehub.warehub.infrastructure.product.repository.ProductRepository;
import com.warehub.warehub.usecase.product.CreateProductUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CreateProductUseCaseImpl implements CreateProductUseCase {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductImageRepository productImageRepository;

    public CreateProductUseCaseImpl(ProductRepository productRepository, ProductCategoryRepository productCategoryRepository, ProductImageRepository productImageRepository) {
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.productImageRepository = productImageRepository;
    }

    @Transactional
    @Override
    public ProductDetailResponseDTO createProduct(ProductRequestDTO req) {

        Optional<Product> duplicateProduct = productRepository.findByNameIgnoreCaseAndDeletedAtIsNull(req.getName());
        if (duplicateProduct.isPresent()){
            throw new DuplicateProductException("Product with name "+ req.getName() + " already exist !");
        }
        
        ProductCategory productCategory = productCategoryRepository.findByIdAndDeletedAtIsNull(req.getProductCategoryId())
                .orElseThrow(()-> new ProductCategoryNotFoundException("Product category with ID "+ req.getProductCategoryId() + " not found !"));

        if (req.getImages().size() > 5){
            throw new MaxListSizeExceededException("Maximum number of product images exceeded !");
        }

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
