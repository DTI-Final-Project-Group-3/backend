package com.warehub.warehub.usecase.user.impl;

import com.warehub.warehub.entity.User;
import com.warehub.warehub.infrastructure.users.dto.UserDetailRequestDTO;
import com.warehub.warehub.infrastructure.users.dto.UserDetailResponseDTO;
import com.warehub.warehub.infrastructure.users.repository.UsersRepository;
import com.warehub.warehub.usecase.user.UserDetailUsecase;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;


@Service
public class UserDetailUsecaseImpl implements UserDetailUsecase {

    private final UsersRepository usersRepository;

    public UserDetailUsecaseImpl(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public UserDetailResponseDTO getUserDetail(JwtAuthenticationToken authToken) {
        User user = getUser(authToken);
        return new UserDetailResponseDTO().copyFromUser(user);
    }

    @Override
    public User getUser(JwtAuthenticationToken authToken) {
        Jwt jwt = authToken.getToken();
        Long id = jwt.getClaim("userId");
        return usersRepository.findById(id).get();
    }

    @Override
    public UserDetailResponseDTO updateUserDetail(JwtAuthenticationToken authToken, UserDetailRequestDTO req) {
        User user = getUser(authToken);
        if (req.getFullname() != null)
            user.setFullname(req.getFullname());
        if (req.getGender() != null)
            user.setGender(req.getGender());
        if (req.getBirthdate() != null)
            user.setBirthdate(req.getBirthdate());
        usersRepository.save(user);
        return getUserDetail(authToken);
    }
}
