package com.warehub.warehub.infrastructure.login.dto;

import lombok.Data;

@Data
public class GoogleLoginRequestDTO {
    private String email;
    private String name;
    private String profilePictureUrl;
    private String accessToken;
    private String provider;
    private String providerUserId;
}
