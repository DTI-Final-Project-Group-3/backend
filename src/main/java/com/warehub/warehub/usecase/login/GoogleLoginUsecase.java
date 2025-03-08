package com.warehub.warehub.usecase.login;

import com.warehub.warehub.infrastructure.login.dto.GoogleLoginRequestDTO;
import com.warehub.warehub.infrastructure.login.dto.LoginResponseDTO;

public interface GoogleLoginUsecase {
    LoginResponseDTO login(GoogleLoginRequestDTO requestDTO);
}
