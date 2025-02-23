package com.warehub.warehub.infrastructure.users.dto;

import lombok.Data;

@Data
public class AssignWarehouseResponseDTO {
    private Long id;
    private Long userAssigneeId;
    private Long userAssignerId;
    private Long warehouseId;
}
