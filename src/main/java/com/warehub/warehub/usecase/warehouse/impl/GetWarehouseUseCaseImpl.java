package com.warehub.warehub.usecase.warehouse.impl;

import com.warehub.warehub.common.enums.LocationConstants;
import com.warehub.warehub.common.utils.Location;
import com.warehub.warehub.common.utils.LocationService;
import com.warehub.warehub.common.utils.ValidationService;
import com.warehub.warehub.entity.Warehouse;
import com.warehub.warehub.infrastructure.warehouse.dto.NearbyWarehouseQuantityResponseDTO;
import com.warehub.warehub.infrastructure.warehouse.dto.NearbyWarehouseRequestDTO;
import com.warehub.warehub.infrastructure.warehouse.dto.NearbyWarehouseResponseDTO;
import com.warehub.warehub.infrastructure.warehouse.dto.WarehouseDetailResponseDTO;
import com.warehub.warehub.infrastructure.warehouse.repository.WarehouseRepository;
import com.warehub.warehub.usecase.warehouse.GetWarehouseUseCase;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetWarehouseUseCaseImpl implements GetWarehouseUseCase {

    private final ValidationService validationService;
    private final WarehouseRepository warehouseRepository;

    public GetWarehouseUseCaseImpl(ValidationService validationService, WarehouseRepository warehouseRepository) {
        this.validationService = validationService;
        this.warehouseRepository = warehouseRepository;
    }

    @Override
    public List<WarehouseDetailResponseDTO> getAllWarehouse() {
        List<Warehouse> warehouses = warehouseRepository.findAllByDeletedAtIsNull();
        return warehouses.stream().map(WarehouseDetailResponseDTO::new).toList();
    }

    @Override
    public WarehouseDetailResponseDTO getWarehouseById(Long warehouseId) {
        Warehouse warehouse = validationService.validateWarehouseId(warehouseId, "Warehouse");

        return new WarehouseDetailResponseDTO(warehouse);
    }

    @Override
    public List<NearbyWarehouseResponseDTO> getNearbyWarehouses(NearbyWarehouseRequestDTO req) {

        validationService.validateProductId(req.getProductId());
        Location location = LocationService.validateLocation(req.getLongitude(), req.getLatitude());

        List<Object[]> nearbyWarehouses = warehouseRepository.findNearbyWarehousesByCoordinate(location.getLongitude(), location.getLatitude(), LocationConstants.MAX_DISTANCE_IN_METERS.getValue(), req.getProductId());

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
    public List<NearbyWarehouseQuantityResponseDTO> getNearbyWarehouseByProductId(Long warehouseId, Long productId) {

        return warehouseRepository.findNearbyWarehouseByWarehouseIdAndProductId(warehouseId, productId);
    }

}
