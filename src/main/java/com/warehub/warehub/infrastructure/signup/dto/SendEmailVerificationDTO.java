package com.warehub.warehub.infrastructure.signup.dto;

import com.warehub.warehub.entity.EmailVerificationToken;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class SendEmailVerificationDTO {
    private Long userId;
    private String token;
    private OffsetDateTime expiresAt;
    private String verificationLink;
    private EmailVerificationToken emailVerificationToken;
}
