package com.warehub.warehub.usecase.user;

import com.warehub.warehub.entity.User;
import com.warehub.warehub.infrastructure.users.dto.UserDetailRequestDTO;
import com.warehub.warehub.infrastructure.users.dto.UserDetailResponseDTO;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public interface UserDetailUsecase {
    UserDetailResponseDTO getUserDetail(JwtAuthenticationToken authToken);
    User getUser(JwtAuthenticationToken authToken);
    UserDetailResponseDTO updateUserDetail(JwtAuthenticationToken authToken, UserDetailRequestDTO req);
}
