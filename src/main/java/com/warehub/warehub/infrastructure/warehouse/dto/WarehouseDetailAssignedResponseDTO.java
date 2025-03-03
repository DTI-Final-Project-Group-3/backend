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

    public WarehouseDetailAssignedResponseDTO(Warehouse warehouse, List<WarehouseAdmin> warehouseAdmins){
        super(warehouse);
        assignedAdmins = new ArrayList<>();
        for (int loop = 0; loop < warehouseAdmins.size(); loop++) {
            WarehouseAdmin warehouseAdmin = warehouseAdmins.get(loop);
            assignedAdmins.add(new AssignedAdmin(warehouseAdmin.getUserAssignee().getId(),
                    warehouseAdmin.getUserAssigner().getId()));
        }
    }
}