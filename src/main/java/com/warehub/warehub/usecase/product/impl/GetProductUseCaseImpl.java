package com.warehub.warehub.usecase.product.impl;

import com.warehub.warehub.common.enums.LocationConstants;
import com.warehub.warehub.common.exceptions.ProductNotFoundException;
import com.warehub.warehub.common.utils.Location;
import com.warehub.warehub.common.utils.LocationService;
import com.warehub.warehub.common.utils.PaginationInfo;
import com.warehub.warehub.entity.Product;
import com.warehub.warehub.entity.ProductImage;
import com.warehub.warehub.entity.Warehouse;
import com.warehub.warehub.entity.WarehouseInventory;
import com.warehub.warehub.infrastructure.product.dto.PaginatedProductRequestDTO;
import com.warehub.warehub.infrastructure.product.dto.PaginatedProductResponseDTO;
import com.warehub.warehub.infrastructure.product.dto.ProductImageResponseDTO;
import com.warehub.warehub.infrastructure.product.dto.ProductResponseDTO;
import com.warehub.warehub.infrastructure.product.repository.ProductImageRepository;
import com.warehub.warehub.infrastructure.product.repository.ProductRepository;
import com.warehub.warehub.infrastructure.warehouse.repository.WarehouseRepository;
import com.warehub.warehub.infrastructure.warehouse_inventories.repository.WarehouseInventoryRepository;
import com.warehub.warehub.usecase.product.GetProductUseCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetProductUseCaseImpl implements GetProductUseCase {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final WarehouseRepository warehouseRepository;
    private final WarehouseInventoryRepository warehouseInventoryRepository;

    public GetProductUseCaseImpl(ProductRepository productRepository, ProductImageRepository productImageRepository, WarehouseRepository warehouseRepository, WarehouseInventoryRepository warehouseInventoryRepository) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.warehouseRepository = warehouseRepository;
        this.warehouseInventoryRepository = warehouseInventoryRepository;
    }

    @Override
    public ProductResponseDTO getProductById(Long productId) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(()-> new ProductNotFoundException("Product with Id " + productId + " not found !"));

        List<ProductImage> productImages = productImageRepository.findByProductIdAndDeletedAtIsNull(productId)
                .stream().toList();

        List<ProductImageResponseDTO> productImageResponseDTOS = productImages.stream().map(ProductImageResponseDTO::new).toList();

        return new ProductResponseDTO(product, productImageResponseDTOS);
    }

    @Override
    public List<ProductResponseDTO> getAllProduct() {
        List<Product> products = productRepository.findAllByDeletedAtIsNull();

        return products.stream().map(product -> {
            List<ProductImageResponseDTO> productImages = productImageRepository.findByProductIdAndDeletedAtIsNull(product.getId()).stream().map(ProductImageResponseDTO::new).toList();
            return new ProductResponseDTO(product, productImages);
        }).toList();
    }

    @Override
    public PaginationInfo<PaginatedProductResponseDTO> getPaginatedProducts(PaginatedProductRequestDTO req) {
        PageRequest pageRequest = PageRequest.of(req.getPage(), req.getLimit());

        Location location = LocationService.validateLocation(req.getLongitude(), req.getLatitude());
        List<Long> nearbyWarehouseIds = warehouseRepository.findNearbyWarehouses(location.getLongitude(), location.getLatitude(), LocationConstants.MAX_DISTANCE_IN_METERS.getValue()).stream().map(Warehouse::getId).toList();

//        Specification<WarehouseInventory> spec = Specification.where(WarehouseInventorySpecification.warehouseIn(nearbyWarehouseIds))
//                .and(WarehouseInventorySpecification.notDeleted())
//                .and(WarehouseInventorySpecification.distinct());
//
//        Page<WarehouseInventory> warehouseInventoryPage = warehouseInventoryRepository.findAll(spec, pageRequest);

        Page<WarehouseInventory> warehouseInventoryPage = warehouseInventoryRepository.findDistinctByProduct(nearbyWarehouseIds, req.getProductCategoryId(), req.getSearchQuery(), pageRequest);

        List<PaginatedProductResponseDTO> productResponseDTOS = warehouseInventoryPage.stream().map(warehouseInventory -> {
            String imageUrl = productImageRepository.findByProductIdAndDeletedAtIsNull(warehouseInventory.getProduct().getId())
                    .stream()
                    .filter(productImage -> productImage.getOrderNumber().equals(1))
                    .findFirst().get().getImageUrl();
            return new PaginatedProductResponseDTO(warehouseInventory, imageUrl);
        }).toList();

      return new PaginationInfo<>(warehouseInventoryPage, productResponseDTOS);
    }
}
