package com.warehub.warehub.usecase.user.impl;


import com.warehub.warehub.common.exceptions.DataNotFoundException;
import com.warehub.warehub.entity.User;
import com.warehub.warehub.infrastructure.users.dto.LoginRequestDTO;
import com.warehub.warehub.infrastructure.users.dto.LoginResponseDTO;
import com.warehub.warehub.infrastructure.users.repository.UsersRepository;
import com.warehub.warehub.usecase.user.LoginUsecase;
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

            String accessToken = tokenGenerationUsecase.generateToken(authentication, 3600L);
            String refreshToken = tokenGenerationUsecase.generateToken(authentication, 604800L);
            long expiresAt = Instant.now().plusSeconds(3600L).toEpochMilli();
            long refreshExpiresAt = Instant.now().plusSeconds(604800L).toEpochMilli();
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
        String newAccessToken = tokenGenerationUsecase.generateToken(email, scope, 3600L);  // 1 hour
        String newRefreshToken = tokenGenerationUsecase.generateToken(email, scope, 604800L);  // 7 days
        long expiresAt = Instant.now().plusSeconds(3600L).toEpochMilli(); // Access token expiry
        long refreshExpiresAt = Instant.now().plusSeconds(604800L).toEpochMilli();
        return new LoginResponseDTO(newAccessToken, newRefreshToken, expiresAt, refreshExpiresAt, user.getRole().getName());
    }
}