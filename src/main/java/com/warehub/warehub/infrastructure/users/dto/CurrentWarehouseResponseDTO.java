package com.warehub.warehub.infrastructure.users.dto;

import lombok.Data;

@Data
public class CurrentWarehouseResponseDTO {
    private Long userAssignerId;
    private Long warehouseId;
}
