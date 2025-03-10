package com.warehub.warehub.usecase.login;


import com.warehub.warehub.infrastructure.login.dto.LoginRequestDTO;
import com.warehub.warehub.infrastructure.login.dto.LoginResponseDTO;

public interface LoginUsecase {
    LoginResponseDTO authenticateUser(LoginRequestDTO req);
    LoginResponseDTO refreshToken(String refreshToken);
}