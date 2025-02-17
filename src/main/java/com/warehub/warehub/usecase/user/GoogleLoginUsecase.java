package com.warehub.warehub.usecase.user;

import com.warehub.warehub.infrastructure.users.dto.GoogleLoginRequestDTO;
import com.warehub.warehub.infrastructure.users.dto.LoginResponseDTO;

public interface GoogleLoginUsecase {
    LoginResponseDTO login(GoogleLoginRequestDTO requestDTO);
}
