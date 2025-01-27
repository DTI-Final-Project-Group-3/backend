package com.warehub.warehub.usecase.user;


import com.warehub.warehub.infrastructure.users.dto.LoginRequestDTO;
import com.warehub.warehub.infrastructure.users.dto.LoginResponseDTO;

public interface LoginUsecase {
    LoginResponseDTO authenticateUser(LoginRequestDTO req);
}