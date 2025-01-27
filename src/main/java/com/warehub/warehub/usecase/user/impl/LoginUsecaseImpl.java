package com.warehub.warehub.usecase.user.impl;


import com.warehub.warehub.common.exceptions.DataNotFoundException;
import com.warehub.warehub.infrastructure.users.dto.LoginRequestDTO;
import com.warehub.warehub.infrastructure.users.dto.LoginResponseDTO;
import com.warehub.warehub.usecase.user.LoginUsecase;
import com.warehub.warehub.usecase.user.TokenGenerationUsecase;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class LoginUsecaseImpl implements LoginUsecase {
    private final AuthenticationManager authenticationManager;
    private final TokenGenerationUsecase tokenService;

    public LoginUsecaseImpl(AuthenticationManager authenticationManager, TokenGenerationUsecase tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @Override
    public LoginResponseDTO authenticateUser(LoginRequestDTO req) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );
            String token = tokenService.generateToken(authentication);
            return new LoginResponseDTO(token);
        } catch (AuthenticationException e) {
            throw new DataNotFoundException("Wrong credentials");
        }
    }
}