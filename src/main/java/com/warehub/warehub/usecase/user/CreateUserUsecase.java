package com.warehub.warehub.usecase.user;

import com.warehub.warehub.entity.enums.RoleType;
import com.warehub.warehub.infrastructure.users.dto.CreateUserRequestDTO;
import com.warehub.warehub.infrastructure.users.dto.UserDetailResponseDTO;

public interface CreateUserUsecase {
    UserDetailResponseDTO createUser(CreateUserRequestDTO req, RoleType roleType);
}
