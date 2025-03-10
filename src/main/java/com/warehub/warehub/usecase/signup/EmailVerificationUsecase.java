package com.warehub.warehub.usecase.signup;

import com.warehub.warehub.infrastructure.signup.dto.EmailVerificationVerifyRequestDTO;
import com.warehub.warehub.infrastructure.signup.dto.EmailVerificationVerifyResponseDTO;
import org.springframework.http.ResponseEntity;

public interface EmailVerificationUsecase {
    ResponseEntity<?> sendEmailVerificationLink(Long userId);
    EmailVerificationVerifyResponseDTO verifyEmailVerificationToken(EmailVerificationVerifyRequestDTO request);
}
