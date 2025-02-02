package com.warehub.warehub.usecase.product.impl;

import com.warehub.warehub.common.exceptions.ProductNotFoundException;
import com.warehub.warehub.common.utils.PaginationInfo;
import com.warehub.warehub.entity.Product;
import com.warehub.warehub.entity.ProductImage;
import com.warehub.warehub.infrastructure.product.dto.ProductImageResponseDTO;
import com.warehub.warehub.infrastructure.product.dto.ProductResponseDTO;
import com.warehub.warehub.infrastructure.product.repository.ProductImageRepository;
import com.warehub.warehub.infrastructure.product.repository.ProductRepository;
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
    public ProductResponseDTO getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new ProductNotFoundException("Product with Id " + productId + " not found !"));

        List<ProductImage> productImages = productImageRepository.findByProductId(productId)
                .stream().toList();

        List<ProductImageResponseDTO> productImageResponseDTOS = productImages.stream().map(ProductImageResponseDTO::new).toList();

        return new ProductResponseDTO(product, productImageResponseDTOS);
    }

    @Override
    public PaginationInfo<ProductResponseDTO> getPaginatedProducts(int page, int limit, Long productCategoryId, List<Integer> sortedWarehouse) {
        PageRequest pageRequest = PageRequest.of(page, limit);

        Specification<Product> spec = Specification.where(null);
        if (productCategoryId != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("productCategory").get("id"), productCategoryId));
        }
        Page<Product> productsPage = productRepository.findAll(spec, pageRequest);

        List<ProductResponseDTO> productResponseDTOS = productsPage.getContent().stream()
                .map(product -> {
                    List<ProductImageResponseDTO> productImageResponseDTOS = productImageRepository.findByProductId(product.getId()).stream()
                            .map(ProductImageResponseDTO::new)
                            .toList();
                    return new ProductResponseDTO(product, productImageResponseDTOS);
                })
                .toList();

        return new PaginationInfo<>(productsPage, productResponseDTOS);
    }


}
