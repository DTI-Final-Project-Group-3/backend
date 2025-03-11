package com.warehub.warehub.usecase.email.impl;

import com.warehub.warehub.entity.EmailVerificationToken;
import com.warehub.warehub.entity.User;
import com.warehub.warehub.infrastructure.email.dto.ChangeEmailGenerateRequestDTO;
import com.warehub.warehub.infrastructure.email.dto.ChangeEmailGenerateResponseDTO;
import com.warehub.warehub.infrastructure.email.dto.ChangeEmailVerifyRequestDTO;
import com.warehub.warehub.infrastructure.email.dto.ChangeEmailVerifyResponseDTO;
import com.warehub.warehub.infrastructure.security.Claims;
import com.warehub.warehub.infrastructure.service.EmailService;
import com.warehub.warehub.infrastructure.signup.dto.*;
import com.warehub.warehub.infrastructure.signup.repository.EmailVerificationTokenRepository;
import com.warehub.warehub.infrastructure.users.repository.UsersRepository;

import com.warehub.warehub.usecase.email.ChangeEmailUsecase;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ChangeEmailUsecaseImpl implements ChangeEmailUsecase {

    @Value("${frontend.email.verification}")
    private String frontendEmailVerification;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Override
    @Transactional
    public ChangeEmailGenerateResponseDTO generateTokenForEmailChange(ChangeEmailGenerateRequestDTO requestDTO) {
        Long userId;
        try {
            userId = Claims.getUserIdFromJwt();
        } catch (Exception e) {
            throw new RuntimeException("User not logged in");
        }

        Optional<User> userOptional = usersRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOptional.get();
        String newEmail = requestDTO.getEmail();

        Optional<EmailVerificationToken> existingToken = emailVerificationTokenRepository.findByUserId(userId);
        if (existingToken.isPresent()) {
            if (existingToken.get().getCreatedAt().isBefore(OffsetDateTime.now().minusMinutes(1))) {
                emailVerificationTokenRepository.delete(existingToken.get());
            } else {
                throw new RuntimeException("Wait for 1 minute before requesting a new email change link");
            }
        }

        String token = "C" + UUID.randomUUID() + "|" + newEmail;
        EmailVerificationToken verificationToken = new EmailVerificationToken();
        verificationToken.setUser(user);
        verificationToken.setToken(token);
        verificationToken.setExpiresAt(OffsetDateTime.now().plusHours(24));
        emailVerificationTokenRepository.save(verificationToken);

        String verificationLink = frontendEmailVerification + "/change-email?token=" + token;
        emailService.sendEmailChangeLink(newEmail, verificationLink);

        ChangeEmailGenerateResponseDTO response = new ChangeEmailGenerateResponseDTO();
        response.setUserId(userId);
        return response;
    }

    @Override
    @Transactional
    public ChangeEmailVerifyResponseDTO verifyToken(ChangeEmailVerifyRequestDTO requestDTO) {
        String token = requestDTO.getToken();

        if (token.charAt(0) != 'C') {
            throw new RuntimeException("Not a change email token");
        }

        String[] parts = token.substring(1).split("\\|", 2);
        if (parts.length != 2) {
            throw new RuntimeException("Invalid email change token.");
        }

        token = "C" + parts[0]; // Keep only UUID part for lookup
        String newEmail = parts[1];

        if (!newEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new RuntimeException("Invalid email format.");
        }

        System.out.println("token = "+token);

        Optional<EmailVerificationToken> optionalToken = emailVerificationTokenRepository.findByToken(requestDTO.getToken());
        if (optionalToken.isEmpty()) {
            throw new RuntimeException("Invalid or expired verification token.");
        }

        EmailVerificationToken verificationToken = optionalToken.get();
        if (verificationToken.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new RuntimeException("Verification token has expired.");
        }

        try {
            User user = usersRepository.findById(verificationToken.getUser().getId()).orElseThrow();
            user.setEmail(newEmail);
            usersRepository.save(user);
            emailVerificationTokenRepository.delete(verificationToken);

            ChangeEmailVerifyResponseDTO response = new ChangeEmailVerifyResponseDTO();
            response.setUserId(user.getId());
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to update email.");
        }
    }
}
