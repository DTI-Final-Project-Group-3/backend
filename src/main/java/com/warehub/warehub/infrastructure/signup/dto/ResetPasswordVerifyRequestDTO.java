package com.warehub.warehub.infrastructure.signup.dto;

import lombok.Data;

@Data
public class ResetPasswordVerifyRequestDTO {
    private String token;
    private String password;
}
