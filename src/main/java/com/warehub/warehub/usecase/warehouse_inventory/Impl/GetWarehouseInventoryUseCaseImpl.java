package com.warehub.warehub.usecase.warehouse_inventory.Impl;

import com.warehub.warehub.common.enums.LocationConstants;
import com.warehub.warehub.common.exceptions.WarehouseInventoryNotFoundException;
import com.warehub.warehub.common.utils.Location;
import com.warehub.warehub.common.utils.LocationService;
import com.warehub.warehub.common.utils.PaginationInfo;
import com.warehub.warehub.entity.WarehouseInventory;
import com.warehub.warehub.infrastructure.product.dto.PaginatedProductResponseDTO;
import com.warehub.warehub.infrastructure.product.dto.ProductImageResponseDTO;
import com.warehub.warehub.infrastructure.product.repository.ProductImageRepository;
import com.warehub.warehub.infrastructure.warehouse.repository.WarehouseRepository;
import com.warehub.warehub.infrastructure.warehouse_inventory.dto.DetailWarehouseInventoryResponseDTO;
import com.warehub.warehub.infrastructure.warehouse_inventory.dto.PaginatedWarehouseInventoryRequestDTO;
import com.warehub.warehub.infrastructure.warehouse_inventory.dto.PaginatedWarehouseInventoryResponseDTO;
import com.warehub.warehub.infrastructure.warehouse_inventory.dto.WarehouseInventoryResponseDTO;
import com.warehub.warehub.infrastructure.warehouse_inventory.repository.WarehouseInventoryRepository;
import com.warehub.warehub.usecase.warehouse_inventory.GetWarehouseInventoryUseCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetWarehouseInventoryUseCaseImpl implements GetWarehouseInventoryUseCase {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseInventoryRepository warehouseInventoryRepository;
    private final ProductImageRepository productImageRepository;

    public GetWarehouseInventoryUseCaseImpl(WarehouseRepository warehouseRepository, WarehouseInventoryRepository warehouseInventoryRepository, ProductImageRepository productImageRepository) {
        this.warehouseRepository = warehouseRepository;
        this.warehouseInventoryRepository = warehouseInventoryRepository;
        this.productImageRepository = productImageRepository;
    }

    @Override
    public DetailWarehouseInventoryResponseDTO getDetailWarehouseInventoryById(Long warehouseInventoryId) {
        WarehouseInventory warehouseInventory = warehouseInventoryRepository.findByIdAndDeletedAtIsNull(warehouseInventoryId)
                .orElseThrow(()-> new WarehouseInventoryNotFoundException("Warehouse inventory with ID " + warehouseInventoryId + " not found !"));

        List<ProductImageResponseDTO> productImageResponseDTO = productImageRepository.findByProductIdAndDeletedAtIsNull(warehouseInventory.getProduct().getId())
                .stream().map(ProductImageResponseDTO::new).toList();

        return new DetailWarehouseInventoryResponseDTO(warehouseInventory, productImageResponseDTO);
    }

    @Override
    public List<WarehouseInventoryResponseDTO> getWarehouseInventoryByWarehouseId(Long warehouseId) {
        List<WarehouseInventory> warehouseInventories = warehouseInventoryRepository.findByWarehouseIdAndDeletedAtIsNull(warehouseId);

        return warehouseInventories.stream().map(WarehouseInventoryResponseDTO::new).toList();
    }

    @Override
    public PaginationInfo<PaginatedWarehouseInventoryResponseDTO> getPaginatedWarehouseInventory(PaginatedWarehouseInventoryRequestDTO req) {
        PageRequest pageRequest = PageRequest.of(req.getPage(), req.getLimit());

        Location location = LocationService.validateLocation(req.getLongitude(), req.getLatitude());
        String nearbyWarehouseIds = warehouseRepository
                .findNearbyWarehouses(
                        location.getLongitude(),
                        location.getLatitude(),
                        LocationConstants.MAX_DISTANCE_IN_METERS.getValue(),null
                )
                .stream()
                .map(nearbyWarehouse -> String.valueOf(nearbyWarehouse[0]))
                .collect(Collectors.joining(",", "{", "}"));

        Page<WarehouseInventory> warehouseInventoryPage = warehouseInventoryRepository.findDistinctByProduct(nearbyWarehouseIds, req.getProductCategoryId(), req.getSearchQuery(), pageRequest);

        List<PaginatedWarehouseInventoryResponseDTO> responseDTOS = warehouseInventoryPage.stream().map(warehouseInventory -> {
            String imageUrl = productImageRepository.findByProductIdAndDeletedAtIsNull(warehouseInventory.getProduct().getId())
                    .stream()
                    .filter(productImage -> productImage.getOrderNumber().equals(1))
                    .findFirst().get().getImageUrl();
            return new PaginatedWarehouseInventoryResponseDTO(warehouseInventory, imageUrl);
        }).toList();

        return new PaginationInfo<>(warehouseInventoryPage, responseDTOS);
    }
}
