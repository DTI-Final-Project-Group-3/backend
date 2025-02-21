package com.warehub.warehub.usecase.product.impl;

import com.warehub.warehub.common.exceptions.ProductNotFoundException;
import com.warehub.warehub.common.utils.Location;
import com.warehub.warehub.common.utils.LocationService;
import com.warehub.warehub.common.utils.PaginationInfo;
import com.warehub.warehub.entity.Product;
import com.warehub.warehub.infrastructure.product.dto.*;
import com.warehub.warehub.infrastructure.product.repository.ProductImageRepository;
import com.warehub.warehub.infrastructure.product.repository.ProductRepository;
import com.warehub.warehub.infrastructure.warehouse.dto.WarehouseResponseDTO;
import com.warehub.warehub.infrastructure.warehouse.repository.WarehouseRepository;
import com.warehub.warehub.infrastructure.warehouseInventory.repository.WarehouseInventoryRepository;
import com.warehub.warehub.usecase.product.GetProductUseCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetProductUseCaseImpl implements GetProductUseCase {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final WarehouseInventoryRepository warehouseInventoryRepository;
    private final WarehouseRepository warehouseRepository;

    public GetProductUseCaseImpl(ProductRepository productRepository, ProductImageRepository productImageRepository, WarehouseInventoryRepository warehouseInventoryRepository, WarehouseRepository warehouseRepository) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.warehouseInventoryRepository = warehouseInventoryRepository;
        this.warehouseRepository = warehouseRepository;
    }

    @Override
    public ProductDetailResponseDTO getProductById(Long productId) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(()-> new ProductNotFoundException("Product with Id " + productId + " not found !"));

        List<ProductImageResponseDTO> productImages = productImageRepository.findByProductIdAndDeletedAtIsNullDTO(productId);

        return new ProductDetailResponseDTO(product, productImages);
    }

    @Override
    public ProductDetailResponseDTO getNearbyProductById(ProductNearbyRequestDTO req) {

        Product product = productRepository.findByIdAndDeletedAtIsNull(req.getProductId())
                .orElseThrow(()-> new ProductNotFoundException("Product with ID " + req.getProductId() + " not found !"));

        Location location = LocationService.validateLocation(req.getLongitude(), req.getLatitude());

        Integer totalStock = warehouseInventoryRepository.findTotalStockNearby(location.getLongitude(), location.getLatitude(), req.getRadius(), req.getProductId());

        WarehouseResponseDTO nearestWarehouse = warehouseRepository.findNearestWarehouseByProductId(location.getLongitude(), location.getLatitude(), req.getRadius(), req.getProductId())
                .orElse(new WarehouseResponseDTO());

        List<ProductImageResponseDTO> productImages = productImageRepository.findByProductIdAndDeletedAtIsNullDTO(req.getProductId());

        ProductDetailResponseDTO productDetailResponseDTO = new ProductDetailResponseDTO(product, productImages);
        productDetailResponseDTO.setTotalStock(totalStock);
        productDetailResponseDTO.setNearestWarehouse(nearestWarehouse);

        return productDetailResponseDTO;
    }

    @Override
    public List<ProductDetailResponseDTO> getAllProduct() {
        List<Product> products = productRepository.findAllByDeletedAtIsNull();

        return products.stream().map(product -> {
            List<ProductImageResponseDTO> productImages = productImageRepository.findByProductIdAndDeletedAtIsNullDTO(product.getId());
            return new ProductDetailResponseDTO(product, productImages);
        }).toList();
    }

    @Override
    public PaginationInfo<ProductSummaryResponseDTO> getPaginatedProducts(ProductPaginationRequestDTO req) {
        PageRequest pageRequest = PageRequest.of(req.getPage(), req.getLimit());

        Page<ProductSummaryResponseDTO> productPage = productRepository.findPaginatedProductsByFilter(req.getProductCategoryId(), req.getSearchQuery(), pageRequest);

        return new PaginationInfo<>(productPage, productPage.getContent());
    }

    @Override
    public PaginationInfo<ProductSummaryResponseDTO> getPaginatedNearbyProducts(ProductPaginationRequestDTO req) {
        PageRequest pageRequest = PageRequest.of(req.getPage(), req.getLimit());

        Location userLocation = LocationService.validateLocation(req.getLongitude(), req.getLatitude());

        Page<ProductSummaryResponseDTO> productPageDTO = productRepository.findPaginatedProductsByUserLocationAndFilter(
                userLocation.getLongitude(), userLocation.getLatitude(), req.getRadius(),
                req.getProductCategoryId(),
                req.getSearchQuery(),
                pageRequest);

        return new PaginationInfo<>(productPageDTO, productPageDTO.getContent());
    }
}
