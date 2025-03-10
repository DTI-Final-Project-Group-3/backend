package com.warehub.warehub.infrastructure.admin.dto;

import com.warehub.warehub.entity.User;
import com.warehub.warehub.infrastructure.users.dto.UserDetailResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAdminDetailResponseDTO extends UserDetailResponseDTO {
    private Long warehouseId;
    private Long userAssignerId;
    private String warehouseName;
    private String userAssignerEmail;

    @Override
    public UserAdminDetailResponseDTO copyFromUser(User user) {
        super.copyFromUser(user);
        warehouseId = -1L;
        userAssignerId = -1L;
        return this;
    }

    public UserAdminDetailResponseDTO(Long id, String username, String email, String fullname, String phoneNumber,
                                      String profileImageUrl, Long warehouseId, Long userAssignerId,
                                      String warehouseName, String userAssignerEmail) {
        setId(id);
        setUsername(username);
        setEmail(email);
        setFullname(fullname);
        setPhoneNumber(phoneNumber);
        setProfileImageUrl(profileImageUrl);
        setWarehouseId(warehouseId);
        setUserAssignerId(userAssignerId);
        setWarehouseName(warehouseName);
        setUserAssignerEmail(userAssignerEmail);
    }
}
