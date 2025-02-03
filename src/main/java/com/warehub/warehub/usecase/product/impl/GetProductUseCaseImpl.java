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
        Product product = productRepository.findActiveById(productId)
                .orElseThrow(()-> new ProductNotFoundException("Product with Id " + productId + " not found !"));

        List<ProductImage> productImages = productImageRepository.findActiveByProductId(productId)
                .stream().toList();

        List<ProductImageResponseDTO> productImageResponseDTOS = productImages.stream().map(ProductImageResponseDTO::new).toList();

        return new ProductResponseDTO(product, productImageResponseDTOS);
    }

    @Override
    public List<ProductResponseDTO> getAllProduct() {
        List<Product> products = productRepository.findActiveAll();

        return products.stream().map(product -> {
            List<ProductImageResponseDTO> productImages = productImageRepository.findActiveByProductId(product.getId()).stream().map(ProductImageResponseDTO::new).toList();
            return new ProductResponseDTO(product, productImages);
        }).toList();
    }

    @Override
    public PaginationInfo<ProductResponseDTO> getPaginatedProducts(int page, int limit, double lng, double lat, Long cat, String search) {
        PageRequest pageRequest = PageRequest.of(page, limit);

        Specification<Product> spec = Specification.where(null);
        if (cat != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("productCategory").get("id"), cat));
        }

        if (search != null && !search.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                String likePattern = "%" + search.toLowerCase() + "%";
                return criteriaBuilder.or( criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), likePattern) );
            });
        }

        Page<Product> productsPage = productRepository.findAll(spec, pageRequest);

        List<ProductResponseDTO> productResponseDTOS = productsPage.getContent().stream()
                .map(product -> {
                    List<ProductImageResponseDTO> productImageResponseDTOS = productImageRepository.findActiveByProductId(product.getId()).stream()
                            .map(ProductImageResponseDTO::new)
                            .toList();
                    return new ProductResponseDTO(product, productImageResponseDTOS);
                })
                .toList();

        return new PaginationInfo<>(productsPage, productResponseDTOS);
    }


}
