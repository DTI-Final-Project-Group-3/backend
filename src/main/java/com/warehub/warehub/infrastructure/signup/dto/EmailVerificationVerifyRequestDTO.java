package com.warehub.warehub.infrastructure.signup.dto;

import lombok.Data;

@Data
public class EmailVerificationVerifyRequestDTO {
    private String token;
    private String password;
}
