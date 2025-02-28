package com.warehub.warehub.infrastructure.users.dto;

import lombok.Data;

@Data
public class EmailVerificationVerifyRequestDTO {
    private String token;
    private String password;
}
