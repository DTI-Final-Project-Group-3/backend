package com.warehub.warehub.infrastructure.warehouse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignedAdmin {
    private long userAssigneeId;
    private long userAssignerId;

    public AssignedAdmin(Long assignee, Long assigner) {
        userAssigneeId = assignee;
        userAssignerId = assigner;
    }
}
