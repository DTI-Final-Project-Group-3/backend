package com.warehub.warehub.usecase.signup;

import com.warehub.warehub.infrastructure.signup.dto.CreateUserRequestDTO;
import com.warehub.warehub.infrastructure.users.dto.UserDetailResponseDTO;

public interface CreateUserUsecase {
    UserDetailResponseDTO createUser(CreateUserRequestDTO req, String role);
}
