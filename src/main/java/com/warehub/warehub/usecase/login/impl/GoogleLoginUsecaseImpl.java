package com.warehub.warehub.usecase.login.impl;

import com.warehub.warehub.entity.User;
import com.warehub.warehub.entity.enums.RoleType;
import com.warehub.warehub.infrastructure.login.dto.GoogleLoginRequestDTO;
import com.warehub.warehub.infrastructure.login.dto.LoginResponseDTO;
import com.warehub.warehub.infrastructure.login.dto.UserAuth;
import com.warehub.warehub.infrastructure.users.repository.RolesRepository;
import com.warehub.warehub.infrastructure.users.repository.UsersRepository;
import com.warehub.warehub.usecase.login.GoogleLoginUsecase;
import com.warehub.warehub.usecase.user.TokenGenerationUsecase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class GoogleLoginUsecaseImpl implements GoogleLoginUsecase {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private TokenGenerationUsecase tokenGenerationUsecase;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RolesRepository rolesRepository;

    @Override
    public LoginResponseDTO login(GoogleLoginRequestDTO requestDTO) {
        Optional<User> optionalUser = usersRepository.findByEmailIgnoreCase(requestDTO.getEmail());
        User user;
        String email;
        if (optionalUser.isEmpty()) {
            // Create a new user if not exists
            user = new User();
            user.setEmail(requestDTO.getEmail());
            user.setFullname(requestDTO.getName());
            user.setProfileImageUrl(requestDTO.getProfilePictureUrl());
            user.setRole(rolesRepository.findByName(RoleType.NOT_VERIFIED.toString()).get()); // Assign default role
            user = usersRepository.save(user);

            email = requestDTO.getEmail();

        } else {
            user = optionalUser.get();
            email = user.getEmail();
        }

        String scope = new UserAuth(user).getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .reduce((a, b) -> a + " " + b)
                .orElse("");

        System.out.println("Google login");

        String accessToken = tokenGenerationUsecase.generateToken(email, scope,3600L);
        String refreshToken = tokenGenerationUsecase.generateToken(email, scope, 604800L);
        long expiresAt = Instant.now().plusSeconds(3600L).toEpochMilli();
        long refreshExpiresAt = Instant.now().plusSeconds(604800L).toEpochMilli();
        return new LoginResponseDTO(accessToken,refreshToken,expiresAt,refreshExpiresAt, user.getRole().getName());
    }
}
