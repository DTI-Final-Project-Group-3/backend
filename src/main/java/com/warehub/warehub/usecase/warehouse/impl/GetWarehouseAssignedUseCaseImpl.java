package com.warehub.warehub.usecase.warehouse.impl;

import com.warehub.warehub.entity.Warehouse;
import com.warehub.warehub.entity.WarehouseAdmin;
import com.warehub.warehub.infrastructure.warehouse.dto.WarehouseDetailAssignedResponseDTO;
import com.warehub.warehub.infrastructure.warehouse.dto.WarehouseDetailResponseDTO;
import com.warehub.warehub.infrastructure.warehouse.repository.WarehouseAdminRepository;
import com.warehub.warehub.infrastructure.warehouse.repository.WarehouseRepository;
import com.warehub.warehub.usecase.warehouse.GetWarehouseAssignedUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GetWarehouseAssignedUseCaseImpl implements GetWarehouseAssignedUseCase {
    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private WarehouseAdminRepository warehouseAdminRepository;

    @Override
    public List<WarehouseDetailAssignedResponseDTO> getAllWarehouseAssigned() {
        List<WarehouseDetailAssignedResponseDTO> result = new ArrayList<>();
        List<Warehouse> warehouses = warehouseRepository.findAllByDeletedAtIsNull();
        for (int loop = 0; loop < warehouses.size(); loop++) {
            List<WarehouseAdmin> warehouseAdmins = warehouseAdminRepository.findByWarehouseId(warehouses.get(loop).getId());
            WarehouseDetailAssignedResponseDTO newResponseDTO = null;
            newResponseDTO = new WarehouseDetailAssignedResponseDTO(warehouses.get(loop), warehouseAdmins);
            result.add(newResponseDTO);
        }
        return result;
    }
}
