package com.warehub.warehub.infrastructure.warehouse.dto;

import com.warehub.warehub.entity.Warehouse;
import com.warehub.warehub.entity.WarehouseAdmin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseDetailAssignedResponseDTO extends WarehouseDetailResponseDTO {
    private List<AssignedAdmin> assignedAdmins;

    public WarehouseDetailAssignedResponseDTO(Warehouse warehouse, List<WarehouseAdmin> warehouseAdmins) {
        super(warehouse);
        assignedAdmins = new ArrayList<>();
        for (WarehouseAdmin warehouseAdmin : warehouseAdmins) {
            addWarehouseAdmin(warehouseAdmin);
        }
    }

    // New method to add a warehouse admin dynamically
    public void addWarehouseAdmin(WarehouseAdmin warehouseAdmin) {
        assignedAdmins.add(new AssignedAdmin(
                warehouseAdmin.getUserAssignee().getId(),
                warehouseAdmin.getUserAssigner().getId()
        ));
    }

    public List<AssignedAdmin> getAssignedAdmins() {
        return assignedAdmins;
    }
}