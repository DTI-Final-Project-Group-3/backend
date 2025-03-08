package com.warehub.warehub.usecase.signup;

import com.warehub.warehub.entity.User;
import com.warehub.warehub.infrastructure.signup.dto.ResetPasswordGenerateRequestDTO;
import com.warehub.warehub.infrastructure.signup.dto.ResetPasswordGenerateResponseDTO;
import com.warehub.warehub.infrastructure.signup.dto.ResetPasswordVerifyRequestDTO;
import com.warehub.warehub.infrastructure.signup.dto.ResetPasswordVerifyResponseDTO;

public interface ResetPasswordUsecase {
    ResetPasswordGenerateResponseDTO generateToken(User user);
    ResetPasswordGenerateResponseDTO generateTokenForEmail(ResetPasswordGenerateRequestDTO request);
    ResetPasswordGenerateResponseDTO generateTokenForLoggedUser();
    ResetPasswordVerifyResponseDTO verifyToken(ResetPasswordVerifyRequestDTO request);
}
