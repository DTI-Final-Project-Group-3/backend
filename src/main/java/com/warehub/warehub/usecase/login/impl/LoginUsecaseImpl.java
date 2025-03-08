package com.warehub.warehub.usecase.login.impl;


import com.warehub.warehub.common.exceptions.DataNotFoundException;
import com.warehub.warehub.entity.User;
import com.warehub.warehub.infrastructure.login.dto.LoginRequestDTO;
import com.warehub.warehub.infrastructure.login.dto.LoginResponseDTO;
import com.warehub.warehub.infrastructure.users.repository.UsersRepository;
import com.warehub.warehub.usecase.login.LoginUsecase;
import com.warehub.warehub.usecase.user.TokenGenerationUsecase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class LoginUsecaseImpl implements LoginUsecase {
    private final AuthenticationManager authenticationManager;
    private final TokenGenerationUsecase tokenGenerationUsecase;

    private final Long SECONDS_1DAY = 86400L;
    private final Long SECONDS_7DAY = 604800L;

    @Autowired
    private UsersRepository usersRepository;

    public LoginUsecaseImpl(AuthenticationManager authenticationManager, TokenGenerationUsecase tokenGenerationUsecase) {
        this.authenticationManager = authenticationManager;
        this.tokenGenerationUsecase = tokenGenerationUsecase;
    }

    @Override
    public LoginResponseDTO authenticateUser(LoginRequestDTO req) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
            );

            String email = authentication.getName();
            User user = usersRepository.findByEmailIgnoreCase(email).get();

            String accessToken = tokenGenerationUsecase.generateToken(authentication, SECONDS_1DAY);
            String refreshToken = tokenGenerationUsecase.generateToken(authentication, SECONDS_7DAY);
            long expiresAt = Instant.now().plusSeconds(SECONDS_1DAY).toEpochMilli();
            long refreshExpiresAt = Instant.now().plusSeconds(SECONDS_7DAY).toEpochMilli();
            return new LoginResponseDTO(accessToken,refreshToken,expiresAt,refreshExpiresAt, user.getRole().getName());
        } catch (AuthenticationException e) {
            throw new DataNotFoundException("Wrong credentials");
        }
    }

    @Override
    public LoginResponseDTO refreshToken(String refreshToken) {
        // Step 1: Verify the refresh token
        if (!tokenGenerationUsecase.tokenVerify(refreshToken)) {
            throw new DataNotFoundException("Invalid or expired refresh token");
        }

        // Step 2: Extract email and scope from the refresh token
        String email = tokenGenerationUsecase.extractEmailFromToken(refreshToken);
        String scope = tokenGenerationUsecase.extractScopeFromToken(refreshToken);
        User user = usersRepository.findByEmailIgnoreCase(email).get();

        // Step 3: Generate new access and refresh tokens
        String newAccessToken = tokenGenerationUsecase.generateToken(email, scope, SECONDS_1DAY);
        String newRefreshToken = tokenGenerationUsecase.generateToken(email, scope, SECONDS_7DAY);
        long expiresAt = Instant.now().plusSeconds(SECONDS_1DAY).toEpochMilli();
        long refreshExpiresAt = Instant.now().plusSeconds(SECONDS_7DAY).toEpochMilli();
        return new LoginResponseDTO(newAccessToken, newRefreshToken, expiresAt, refreshExpiresAt, user.getRole().getName());
    }
}