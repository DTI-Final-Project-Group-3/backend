package com.warehub.warehub.usecase.warehouseInventory.Impl;

import com.warehub.warehub.common.enums.LocationConstants;
import com.warehub.warehub.common.exceptions.WarehouseInventoryNotFoundException;
import com.warehub.warehub.common.utils.Location;
import com.warehub.warehub.common.utils.LocationService;
import com.warehub.warehub.common.utils.PaginationInfo;
import com.warehub.warehub.entity.WarehouseInventory;
import com.warehub.warehub.infrastructure.product.dto.ProductImageResponseDTO;
import com.warehub.warehub.infrastructure.product.repository.ProductImageRepository;
import com.warehub.warehub.infrastructure.warehouse.repository.WarehouseRepository;
import com.warehub.warehub.infrastructure.warehouseInventory.dto.WarehouseInventoryDetailResponseDTO;
import com.warehub.warehub.infrastructure.warehouseInventory.dto.WarehouseInventoryPaginationRequestDTO;
import com.warehub.warehub.infrastructure.warehouseInventory.dto.WarehouseInventorySummaryResponseDTO;
import com.warehub.warehub.infrastructure.warehouseInventory.dto.WarehouseInventoryResponseDTO;
import com.warehub.warehub.infrastructure.warehouseInventory.repository.WarehouseInventoryRepository;
import com.warehub.warehub.usecase.warehouseInventory.GetWarehouseInventoryUseCase;
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
    public WarehouseInventoryDetailResponseDTO getDetailWarehouseInventoryById(Long warehouseInventoryId) {
        WarehouseInventory warehouseInventory = warehouseInventoryRepository.findByIdAndDeletedAtIsNull(warehouseInventoryId)
                .orElseThrow(()-> new WarehouseInventoryNotFoundException("Warehouse inventory with ID " + warehouseInventoryId + " not found !"));

        List<ProductImageResponseDTO> productImageResponseDTO = productImageRepository.findByProductIdAndDeletedAtIsNull(warehouseInventory.getProduct().getId())
                .stream().map(ProductImageResponseDTO::new).toList();

        return new WarehouseInventoryDetailResponseDTO(warehouseInventory, productImageResponseDTO);
    }

    @Override
    public List<WarehouseInventoryResponseDTO> getWarehouseInventoryByWarehouseId(Long warehouseId) {
        List<WarehouseInventory> warehouseInventories = warehouseInventoryRepository.findByWarehouseIdAndDeletedAtIsNull(warehouseId);

        return warehouseInventories.stream().map(WarehouseInventoryResponseDTO::new).toList();
    }

    @Override
    public PaginationInfo<WarehouseInventorySummaryResponseDTO> getPaginatedWarehouseInventory(WarehouseInventoryPaginationRequestDTO req) {
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

        List<WarehouseInventorySummaryResponseDTO> responseDTOS = warehouseInventoryPage.stream().map(warehouseInventory -> {
            String imageUrl = productImageRepository.findByProductIdAndDeletedAtIsNull(warehouseInventory.getProduct().getId())
                    .stream()
                    .filter(productImage -> productImage.getPosition().equals(1))
                    .findFirst().get().getUrl();
            return new WarehouseInventorySummaryResponseDTO(warehouseInventory, imageUrl);
        }).toList();

        return new PaginationInfo<>(warehouseInventoryPage, responseDTOS);
    }
}
