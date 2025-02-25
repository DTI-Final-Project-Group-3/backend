package com.warehub.warehub.usecase.user;

import com.warehub.warehub.entity.User;
import com.warehub.warehub.infrastructure.users.dto.ResetPasswordGenerateResponseDTO;
import com.warehub.warehub.infrastructure.users.dto.ResetPasswordVerifyRequestDTO;
import com.warehub.warehub.infrastructure.users.dto.ResetPasswordVerifyResponseDTO;

public interface ResetPasswordUsecase {
    ResetPasswordGenerateResponseDTO generateToken(User user);
    ResetPasswordGenerateResponseDTO generateToken();
    ResetPasswordVerifyResponseDTO verifyToken(ResetPasswordVerifyRequestDTO request);
}
