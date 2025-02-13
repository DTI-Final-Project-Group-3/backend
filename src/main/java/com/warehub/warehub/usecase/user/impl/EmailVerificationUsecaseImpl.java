package com.warehub.warehub.usecase.user.impl;

import com.warehub.warehub.common.response.ApiResponse;
import com.warehub.warehub.entity.EmailVerificationToken;
import com.warehub.warehub.entity.User;
import com.warehub.warehub.entity.enums.RoleType;
import com.warehub.warehub.infrastructure.service.EmailService;
import com.warehub.warehub.infrastructure.users.dto.SendEmailVerificationDTO;
import com.warehub.warehub.infrastructure.users.repository.EmailVerificationTokenRepository;
import com.warehub.warehub.infrastructure.users.repository.RolesRepository;
import com.warehub.warehub.infrastructure.users.repository.UsersRepository;
import com.warehub.warehub.usecase.user.EmailVerificationUsecase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmailVerificationUsecaseImpl implements EmailVerificationUsecase {

    @Autowired
    private EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private RolesRepository rolesRepository;

    @Override
    public ResponseEntity<?> send(Long userId) {
        try {
            User user = usersRepository.findById(userId).get();

            // Generate a verification token
            String token = UUID.randomUUID().toString();
            EmailVerificationToken verificationToken = new EmailVerificationToken();
            verificationToken.setUser(user);
            verificationToken.setToken(token);
            verificationToken.setExpiresAt(OffsetDateTime.now().plusHours(24));
            emailVerificationTokenRepository.save(verificationToken);

            // Send verification email
            String verificationLink = "http://localhost:8080/api/v1/signup/verify?token=" + token;
            System.out.println("email = " + user.getEmail() + " verificationLink = " + verificationLink);
            emailService.sendVerificationEmail(user.getEmail(), verificationLink);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.successfulResponse("Failed to send email verification link");
        }
        return ApiResponse.successfulResponse("Successfully sent email verification link");
    }

    public ResponseEntity<?> verify(String token) {
        Optional<EmailVerificationToken> optionalToken = emailVerificationTokenRepository.findByToken(token);

        if (optionalToken.isEmpty()) {
            return ApiResponse.failedResponse("Invalid or expired verification token.");
        }

        EmailVerificationToken verificationToken = optionalToken.get();
        if (verificationToken.getExpiresAt().isBefore(OffsetDateTime.now())) {
            return ApiResponse.failedResponse("Verification token has expired.");
        }

        try {

            // Mark user as verified
            User user = usersRepository.findById(verificationToken.getUser().getId()).orElseThrow();
            user.setIsEmailVerified(true);
            user.setRole(rolesRepository.findByName(RoleType.CUSTOMER_VERIFIED.toString()).get());
            usersRepository.save(user);

            // Delete the used token
            emailVerificationTokenRepository.delete(verificationToken);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.failedResponse("Error occured when verifying email");
        }
        return ApiResponse.successfulResponse("Email successfully verified");
    }
}
