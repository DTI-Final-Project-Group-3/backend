package com.warehub.warehub.usecase.email;

import com.warehub.warehub.infrastructure.email.dto.ChangeEmailGenerateRequestDTO;
import com.warehub.warehub.infrastructure.email.dto.ChangeEmailGenerateResponseDTO;
import com.warehub.warehub.infrastructure.email.dto.ChangeEmailVerifyRequestDTO;
import com.warehub.warehub.infrastructure.email.dto.ChangeEmailVerifyResponseDTO;

public interface ChangeEmailUsecase {
    ChangeEmailGenerateResponseDTO generateTokenForEmailChange(ChangeEmailGenerateRequestDTO requestDTO);
    ChangeEmailVerifyResponseDTO verifyToken(ChangeEmailVerifyRequestDTO requestDTO);
}