package com.warehub.warehub.usecase.user;

import com.warehub.warehub.infrastructure.users.dto.AssignWarehouseRequestDTO;
import com.warehub.warehub.infrastructure.users.dto.UserAdminDetailResponseDTO;
import com.warehub.warehub.infrastructure.users.dto.AssignWarehouseResponseDTO;

import java.util.List;

public interface AdminUsecase {
    List<UserAdminDetailResponseDTO> getAllAdminWarehouse();
    List<UserAdminDetailResponseDTO> getAllAdminWarehouseNotAssigned();
    List<UserAdminDetailResponseDTO> getAllAdminWarehouseAssigned(Long warehouseId);
    AssignWarehouseResponseDTO assignWarehouse(AssignWarehouseRequestDTO request);
    AssignWarehouseResponseDTO removeWarehouseAssignment(AssignWarehouseRequestDTO request);
}
