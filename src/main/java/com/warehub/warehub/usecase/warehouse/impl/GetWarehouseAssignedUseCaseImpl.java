package com.warehub.warehub.usecase.warehouse.impl;

import com.warehub.warehub.entity.Warehouse;
import com.warehub.warehub.entity.WarehouseAdmin;
import com.warehub.warehub.infrastructure.warehouse.dto.WarehouseDetailAssignedResponseDTO;
import com.warehub.warehub.infrastructure.warehouse.repository.WarehouseAdminRepository;
import com.warehub.warehub.infrastructure.warehouse.repository.WarehouseRepository;
import com.warehub.warehub.usecase.warehouse.GetWarehouseAssignedUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GetWarehouseAssignedUseCaseImpl implements GetWarehouseAssignedUseCase {
    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private WarehouseAdminRepository warehouseAdminRepository;

    @Override
    public List<WarehouseDetailAssignedResponseDTO> getAllWarehouseWithAssignedAdminOnly() {
        List<Warehouse> warehouses = warehouseRepository.findAllWarehousesWithAssignedAdmins();
        List<WarehouseAdmin> warehouseAdmins = warehouseAdminRepository.findAllWarehouseAdminsWithWarehouse();

        Map<Long, WarehouseDetailAssignedResponseDTO> warehouseMap = new HashMap<>();

        for (Warehouse warehouse : warehouses) {
            warehouseMap.put(warehouse.getId(), new WarehouseDetailAssignedResponseDTO(warehouse, new ArrayList<>()));
        }

        for (WarehouseAdmin wa : warehouseAdmins) {
            WarehouseDetailAssignedResponseDTO dto = warehouseMap.get(wa.getWarehouse().getId());
            if (dto != null) {
                dto.addWarehouseAdmin(wa);
            }
        }
        return new ArrayList<>(warehouseMap.values());
    }

    @Override
    public List<WarehouseDetailAssignedResponseDTO> getAllWarehouseEmptyAssignedAdmin() {
        List<Warehouse> warehouses = warehouseRepository.findAllWarehousesWithoutAdmins();

        return warehouses.stream()
                .map(warehouse -> new WarehouseDetailAssignedResponseDTO(warehouse, new ArrayList<>()))
                .toList();
    }

    @Override
    public List<WarehouseDetailAssignedResponseDTO> getAllWarehouseAssignedAndEmpty() {
        List<Warehouse> warehouses = warehouseRepository.findAllByDeletedAtIsNull();
        List<WarehouseAdmin> warehouseAdmins = warehouseAdminRepository.findAllWarehouseAdminsWithWarehouse();

        Map<Long, WarehouseDetailAssignedResponseDTO> warehouseMap = new HashMap<>();

        for (Warehouse warehouse : warehouses) {
            warehouseMap.put(warehouse.getId(), new WarehouseDetailAssignedResponseDTO(warehouse, new ArrayList<>()));
        }

        for (WarehouseAdmin wa : warehouseAdmins) {
            WarehouseDetailAssignedResponseDTO dto = warehouseMap.get(wa.getWarehouse().getId());
            if (dto != null) {
                dto.addWarehouseAdmin(wa);
            }
        }

        return new ArrayList<>(warehouseMap.values());
    }

}
