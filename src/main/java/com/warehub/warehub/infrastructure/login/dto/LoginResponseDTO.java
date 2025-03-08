package com.warehub.warehub.infrastructure.login.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpires;
    private Long refreshTokenExpires;
    private String role;
}
