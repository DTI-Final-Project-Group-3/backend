package com.warehub.warehub.usecase.product.impl;

import com.warehub.warehub.common.exceptions.ProductNotFoundException;
import com.warehub.warehub.common.utils.PaginationInfo;
import com.warehub.warehub.entity.Product;
import com.warehub.warehub.entity.ProductImage;
import com.warehub.warehub.infrastructure.product.dto.ProductPaginationRequestDTO;
import com.warehub.warehub.infrastructure.product.dto.ProductSummaryResponseDTO;
import com.warehub.warehub.infrastructure.product.dto.ProductImageResponseDTO;
import com.warehub.warehub.infrastructure.product.dto.ProductDetailResponseDTO;
import com.warehub.warehub.infrastructure.product.repository.ProductImageRepository;
import com.warehub.warehub.infrastructure.product.repository.ProductRepository;
import com.warehub.warehub.infrastructure.product.specification.ProductSpecification;
import com.warehub.warehub.usecase.product.GetProductUseCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetProductUseCaseImpl implements GetProductUseCase {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;

    public GetProductUseCaseImpl(ProductRepository productRepository, ProductImageRepository productImageRepository) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
    }

    @Override
    public ProductDetailResponseDTO getProductById(Long productId) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(()-> new ProductNotFoundException("Product with Id " + productId + " not found !"));

        List<ProductImage> productImages = productImageRepository.findByProductIdAndDeletedAtIsNull(productId)
                .stream().toList();

        List<ProductImageResponseDTO> productImageResponseDTOS = productImages.stream().map(ProductImageResponseDTO::new).toList();

        return new ProductDetailResponseDTO(product, productImageResponseDTOS);
    }

    @Override
    public List<ProductDetailResponseDTO> getAllProduct() {
        List<Product> products = productRepository.findAllByDeletedAtIsNull();

        return products.stream().map(product -> {
            List<ProductImageResponseDTO> productImages = productImageRepository.findByProductIdAndDeletedAtIsNull(product.getId()).stream().map(ProductImageResponseDTO::new).toList();
            return new ProductDetailResponseDTO(product, productImages);
        }).toList();
    }

    @Override
    public PaginationInfo<ProductSummaryResponseDTO> getPaginatedProducts(ProductPaginationRequestDTO req) {
        PageRequest pageRequest = PageRequest.of(req.getPage(), req.getLimit());

        Specification<Product> spec = Specification.where(ProductSpecification.productCategory(req.getProductCategoryId()))
                .and(ProductSpecification.notDeleted())
                .and(ProductSpecification.searchQuery(req.getSearchQuery()));

        Page<Product> productPage = productRepository.findAll(spec, pageRequest);

        List<ProductSummaryResponseDTO> responseDTOS = productPage.stream().map(product -> {
            ProductImage productImage = productImageRepository.findByProductIdAndDeletedAtIsNull(product.getId())
                    .stream().filter(image -> image.getPosition().equals(1))
                    .findFirst()
                    .orElse(new ProductImage());
            return new ProductSummaryResponseDTO(product, productImage.getUrl());
        }).toList();

        return new PaginationInfo<>(productPage, responseDTOS);
    }
}
