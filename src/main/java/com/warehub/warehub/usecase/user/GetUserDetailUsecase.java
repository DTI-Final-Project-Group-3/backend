package com.warehub.warehub.usecase.user;

import com.warehub.warehub.infrastructure.users.dto.UserDetailResponseDTO;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public interface GetUserDetailUsecase {
    UserDetailResponseDTO getUserDetail(JwtAuthenticationToken authToken);
}
