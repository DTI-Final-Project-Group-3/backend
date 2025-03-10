package com.warehub.warehub.infrastructure.admin.dto;

import lombok.Data;

@Data
public class UserAdminUpdateRequestDTO {
    private String password;
    private String fullname;
    private String profileImageUrl;
}
