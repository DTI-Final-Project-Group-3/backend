package com.warehub.warehub.usecase.admin;

import com.warehub.warehub.infrastructure.admin.dto.AssignWarehouseRequestDTO;
import com.warehub.warehub.infrastructure.users.dto.CurrentWarehouseResponseDTO;
import com.warehub.warehub.infrastructure.admin.dto.UserAdminDetailResponseDTO;
import com.warehub.warehub.infrastructure.admin.dto.AssignWarehouseResponseDTO;

import java.util.List;

public interface AdminUsecase {
    List<UserAdminDetailResponseDTO> getAllAdminWarehouse();
    List<UserAdminDetailResponseDTO> getAllAdminWarehouseNotAssigned();
    List<UserAdminDetailResponseDTO> getAllAdminWarehouseAssigned(Long warehouseId);
    AssignWarehouseResponseDTO assignWarehouse(AssignWarehouseRequestDTO request);
    AssignWarehouseResponseDTO removeWarehouseAssignment(AssignWarehouseRequestDTO request);
    CurrentWarehouseResponseDTO getCurrentWarehouseDTO();

    String deleteAdmin(Long userId);
}
