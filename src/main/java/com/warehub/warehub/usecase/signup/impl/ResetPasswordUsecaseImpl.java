package com.warehub.warehub.usecase.signup.impl;

import com.warehub.warehub.entity.EmailVerificationToken;
import com.warehub.warehub.entity.User;
import com.warehub.warehub.infrastructure.security.Claims;
import com.warehub.warehub.infrastructure.service.EmailService;
import com.warehub.warehub.infrastructure.signup.dto.ResetPasswordGenerateRequestDTO;
import com.warehub.warehub.infrastructure.signup.dto.ResetPasswordGenerateResponseDTO;
import com.warehub.warehub.infrastructure.signup.dto.ResetPasswordVerifyRequestDTO;
import com.warehub.warehub.infrastructure.signup.dto.ResetPasswordVerifyResponseDTO;
import com.warehub.warehub.infrastructure.signup.repository.EmailVerificationTokenRepository;
import com.warehub.warehub.infrastructure.users.repository.UsersRepository;
import com.warehub.warehub.usecase.signup.ResetPasswordUsecase;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

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

    @Autowired
    private EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Transactional
    public ResetPasswordGenerateResponseDTO generateToken(User user) {
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(1800);
        Long userId = user.getId();
        String email = user.getEmail();

        /*
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .subject(email)
                .claim("userId", userId)
                .expiresAt(expiration)
                .build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
         */

        Optional<EmailVerificationToken> emailVerificationToken = emailVerificationTokenRepository.findByUserId(user.getId());

        if (emailVerificationToken.isPresent()) {
            if (emailVerificationToken.get().getCreatedAt().isBefore(OffsetDateTime.now().minusMinutes(1)))
                emailVerificationTokenRepository.delete(emailVerificationToken.get());
            else
                throw new RuntimeException("Wait for 1 minute before requesting a new email verification link");
        }

        String token = "R" + UUID.randomUUID().toString();
        EmailVerificationToken verificationToken = new EmailVerificationToken();
        verificationToken.setUser(user);
        verificationToken.setToken(token);
        verificationToken.setExpiresAt(OffsetDateTime.now().plusHours(24));
        emailVerificationTokenRepository.save(verificationToken);

        String verificationLink = "http://localhost:3000/reset-password?token=" + token;
        System.out.println(verificationLink);
        emailService.sendResetPasswordEmail(email, verificationLink);

        ResetPasswordGenerateResponseDTO response = new ResetPasswordGenerateResponseDTO();
        response.setUserId(userId);
        return response;
    }

    @Override
    public ResetPasswordGenerateResponseDTO generateTokenForEmail(ResetPasswordGenerateRequestDTO requestDTO) {
        Optional<User> user = usersRepository.findByEmailIgnoreCase(requestDTO.getEmail());
        return generateToken(user.get());
    }

    @Override
    public ResetPasswordGenerateResponseDTO generateTokenForLoggedUser() {
        Long userId;
        try {
            userId = Claims.getUserIdFromJwt();
        } catch (Exception e) {
            throw new RuntimeException("User not logged in");
        }
        Optional<User> user = usersRepository.findById(userId);
        return generateToken(user.get());
    }

    @Override
    @Transactional
    public ResetPasswordVerifyResponseDTO verifyToken(ResetPasswordVerifyRequestDTO request) {
        ResetPasswordVerifyResponseDTO response = new ResetPasswordVerifyResponseDTO();
        String token = request.getToken();

        if (request.getPassword().length() < 8) {
            throw new RuntimeException("Password length minimum is 8 characters");
        }

        if (token.charAt(0) != 'R') {
            throw new RuntimeException("Not a reset password token");
        }

        Optional<EmailVerificationToken> optionalToken = emailVerificationTokenRepository.findByToken(token);

        if (optionalToken.isEmpty()) {
            throw new RuntimeException("Invalid or expired verification token.");
        }

        EmailVerificationToken verificationToken = optionalToken.get();
        if (verificationToken.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new RuntimeException("Verification token has expired.");
        }

        try {
            User user = usersRepository.findById(verificationToken.getUser().getId()).orElseThrow();
            Long userId = user.getId();
            user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            usersRepository.save(user);
            emailVerificationTokenRepository.delete(verificationToken);
            response.setUserId(userId);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set a new password");
        }
    }
}
