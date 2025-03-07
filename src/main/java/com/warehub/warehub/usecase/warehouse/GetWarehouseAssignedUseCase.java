package com.warehub.warehub.usecase.warehouse;

import com.warehub.warehub.infrastructure.warehouse.dto.WarehouseDetailAssignedResponseDTO;
import com.warehub.warehub.infrastructure.warehouse.dto.WarehouseDetailResponseDTO;

import java.util.List;

public interface GetWarehouseAssignedUseCase {
    List<WarehouseDetailAssignedResponseDTO> getAllWarehouseWithAssignedAdminOnly();
    List<WarehouseDetailAssignedResponseDTO> getAllWarehouseEmptyAssignedAdmin();
    List<WarehouseDetailAssignedResponseDTO> getAllWarehouseAssignedAndEmpty();
}
