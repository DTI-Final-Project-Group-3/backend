package com.warehub.warehub.usecase.user.impl;

import com.warehub.warehub.entity.User;
import com.warehub.warehub.infrastructure.users.dto.UserAuth;
import com.warehub.warehub.infrastructure.users.dto.UserDetailResponseDTO;
import com.warehub.warehub.infrastructure.users.repository.UsersRepository;
import com.warehub.warehub.usecase.user.GetUserDetailUsecase;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;


@Service
public class GetUserDetailUsecaseImpl implements GetUserDetailUsecase {

    private final UsersRepository usersRepository;

    public GetUserDetailUsecaseImpl(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public UserDetailResponseDTO getUserDetail(JwtAuthenticationToken authToken) {
        Jwt jwt = authToken.getToken();
        Long id = jwt.getClaim("userId");
        User user = usersRepository.findById(id).get();
        return new UserDetailResponseDTO().copyFromUser(user);
    }
}
