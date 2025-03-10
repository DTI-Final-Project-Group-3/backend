package com.warehub.warehub.infrastructure.admin.dto;

import lombok.Data;

@Data
public class AssignWarehouseRequestDTO {
    private Long userAssigneeId;
    private Long warehouseId;
}
