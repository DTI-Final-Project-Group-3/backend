package com.warehub.warehub.usecase.user;

import com.warehub.warehub.infrastructure.users.dto.EmailVerificationVerifyRequestDTO;
import com.warehub.warehub.infrastructure.users.dto.EmailVerificationVerifyResponseDTO;
import org.springframework.http.ResponseEntity;

public interface EmailVerificationUsecase {
    ResponseEntity<?> sendEmailVerificationLink(Long userId);
    EmailVerificationVerifyResponseDTO verifyEmailVerificationToken(EmailVerificationVerifyRequestDTO request);
}
