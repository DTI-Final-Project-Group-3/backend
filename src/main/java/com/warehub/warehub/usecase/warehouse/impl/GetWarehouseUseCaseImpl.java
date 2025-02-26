package com.warehub.warehub.usecase.warehouse.impl;

import com.warehub.warehub.common.enums.LocationConstants;
import com.warehub.warehub.common.exceptions.ProductNotFoundException;
import com.warehub.warehub.common.exceptions.WarehouseNotFoundException;
import com.warehub.warehub.common.utils.Location;
import com.warehub.warehub.common.utils.LocationService;
import com.warehub.warehub.entity.Warehouse;
import com.warehub.warehub.infrastructure.product.repository.ProductRepository;
import com.warehub.warehub.infrastructure.warehouse.dto.NearbyWarehouseRequestDTO;
import com.warehub.warehub.infrastructure.warehouse.dto.NearbyWarehouseResponseDTO;
import com.warehub.warehub.infrastructure.warehouse.dto.WarehouseDetailResponseDTO;
import com.warehub.warehub.infrastructure.warehouse.repository.WarehouseRepository;
import com.warehub.warehub.usecase.warehouse.GetWarehouseUseCase;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetWarehouseUseCaseImpl implements GetWarehouseUseCase {

    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;

    public GetWarehouseUseCaseImpl(WarehouseRepository warehouseRepository, ProductRepository productRepository) {
        this.warehouseRepository = warehouseRepository;
        this.productRepository = productRepository;
    }

    @Override
    public List<WarehouseDetailResponseDTO> getAllWarehouse() {
        List<Warehouse> warehouses = warehouseRepository.findAllByDeletedAtIsNull();
        return warehouses.stream().map(WarehouseDetailResponseDTO::new).toList();
    }

    @Override
    public WarehouseDetailResponseDTO getWarehouseById(Long warehouseId) {
        Warehouse warehouse = warehouseRepository.findByIdAndDeletedAtIsNull(warehouseId)
                .orElseThrow(()-> new WarehouseNotFoundException("Warehouse with ID " + warehouseId + " not found !"));

        return new WarehouseDetailResponseDTO(warehouse);
    }

    @Override
    public List<NearbyWarehouseResponseDTO> getNearbyWarehouses(NearbyWarehouseRequestDTO req) {

        if (req.getProductId() != null){
            productRepository.findByIdAndDeletedAtIsNull(req.getProductId()).orElseThrow(()-> new ProductNotFoundException("Product with ID "+ req.getProductId() + " not found !"));
        }
        Location location = LocationService.validateLocation(req.getLongitude(), req.getLatitude());

        List<Object[]> nearbyWarehouses = warehouseRepository.findNearbyWarehouses(location.getLongitude(), location.getLatitude(), LocationConstants.MAX_DISTANCE_IN_METERS.getValue(), req.getProductId());

        return nearbyWarehouses.stream()
                .map(nearbyWarehouse -> {
                    NearbyWarehouseResponseDTO nearbyWarehouseResponseDTO = new NearbyWarehouseResponseDTO();
                    nearbyWarehouseResponseDTO.setId((Long) nearbyWarehouse[0]);
                    nearbyWarehouseResponseDTO.setName((String) nearbyWarehouse[1]);
                    nearbyWarehouseResponseDTO.setLongitude((Double) nearbyWarehouse[2]);
                    nearbyWarehouseResponseDTO.setLatitude((Double) nearbyWarehouse[3]);
                    nearbyWarehouseResponseDTO.setDistanceInMeters((Double) nearbyWarehouse[4]);
                    return nearbyWarehouseResponseDTO;
                }).toList();
    }

    @Override
    public List<NearbyWarehouseResponseDTO> getNearbyWarehouseByProductId(NearbyWarehouseRequestDTO req) {
        return null;
    }
}
