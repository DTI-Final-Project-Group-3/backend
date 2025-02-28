package com.warehub.warehub.usecase.user.impl;

import com.warehub.warehub.entity.User;
import com.warehub.warehub.infrastructure.security.Claims;
import com.warehub.warehub.infrastructure.service.EmailService;
import com.warehub.warehub.infrastructure.users.dto.ResetPasswordGenerateResponseDTO;
import com.warehub.warehub.infrastructure.users.dto.ResetPasswordVerifyRequestDTO;
import com.warehub.warehub.infrastructure.users.dto.ResetPasswordVerifyResponseDTO;
import com.warehub.warehub.infrastructure.users.repository.UsersRepository;
import com.warehub.warehub.usecase.user.ResetPasswordUsecase;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ResetPasswordUsecaseImpl implements ResetPasswordUsecase {
    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public ResetPasswordGenerateResponseDTO generateToken(User user) {
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(1800);
        Long userId = -1L;
        String email;

        if (user != null) {
            userId = user.getId();
            email = user.getEmail();
        } else {
            try {
                userId = Claims.getUserIdFromJwt();
                email = usersRepository.findById(userId).get().getEmail();
            } catch (Exception e) {
                throw new RuntimeException("User not logged in");
            }
        }

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .subject(email)
                .claim("userId", userId)
                .expiresAt(expiration)
                .build();

        ResetPasswordGenerateResponseDTO response = new ResetPasswordGenerateResponseDTO();
        response.setUserId(userId);
        String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        String verificationLink = "http://localhost:3000/reset-password?token=" + token;
        System.out.println(verificationLink);
        emailService.sendVerificationEmail(email, verificationLink);
        return response;
    }

    @Override
    public ResetPasswordGenerateResponseDTO generateToken() {
        return generateToken(null);
    }

    @Override
    @Transactional
    public ResetPasswordVerifyResponseDTO verifyToken(ResetPasswordVerifyRequestDTO request) {
        ResetPasswordVerifyResponseDTO response = new ResetPasswordVerifyResponseDTO();
        try {
            System.out.println("token = " + request.getToken());
            Jwt jwt = jwtDecoder.decode(request.getToken());
            Long userId = jwt.getClaim("userId");
            User user = usersRepository.findById(userId).get();
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            usersRepository.save(user);
            response.setUserId(userId);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set a new password");
        }
    }
}
