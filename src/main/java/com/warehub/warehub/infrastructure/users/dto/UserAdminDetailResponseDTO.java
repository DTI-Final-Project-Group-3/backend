package com.warehub.warehub.infrastructure.users.dto;

import com.warehub.warehub.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAdminDetailResponseDTO extends  UserDetailResponseDTO {
    private Long warehouseId;
    private Long userAssignerId;

    @Override
    public UserAdminDetailResponseDTO copyFromUser(User user) {
        super.copyFromUser(user);
        warehouseId = -1L;
        userAssignerId = -1L;
        return this;
    }
}
