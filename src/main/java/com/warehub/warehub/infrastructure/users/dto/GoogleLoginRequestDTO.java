package com.warehub.warehub.infrastructure.users.dto;

import lombok.Data;

@Data
public class GoogleLoginRequestDTO {
    private String email;
    private String name;
    private String profilePictureUrl;
}
