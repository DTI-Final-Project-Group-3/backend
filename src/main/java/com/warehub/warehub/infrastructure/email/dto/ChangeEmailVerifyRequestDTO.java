package com.warehub.warehub.infrastructure.email.dto;
import lombok.Data;

@Data
public class ChangeEmailVerifyRequestDTO {
    private String token;
}
