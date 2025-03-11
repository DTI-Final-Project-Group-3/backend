package com.warehub.warehub.infrastructure.email.controller;

import com.warehub.warehub.common.response.ApiResponse;
import com.warehub.warehub.infrastructure.email.dto.ChangeEmailGenerateRequestDTO;
import com.warehub.warehub.infrastructure.email.dto.ChangeEmailGenerateResponseDTO;
import com.warehub.warehub.infrastructure.email.dto.ChangeEmailVerifyRequestDTO;
import com.warehub.warehub.infrastructure.email.dto.ChangeEmailVerifyResponseDTO;
import com.warehub.warehub.infrastructure.signup.dto.EmailVerificationVerifyRequestDTO;
import com.warehub.warehub.infrastructure.signup.dto.EmailVerificationVerifyResponseDTO;
import com.warehub.warehub.usecase.email.ChangeEmailUsecase;
import com.warehub.warehub.usecase.signup.EmailVerificationUsecase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user/email")
public class ChangeEmailController {
    @Autowired
    private ChangeEmailUsecase changeEmailUsecase;

    @PostMapping("/request-email-change")
    public ResponseEntity<?> changeEmail(@RequestBody ChangeEmailGenerateRequestDTO request) {
        ChangeEmailGenerateResponseDTO result = null;
        String errorMessage = "";
        try {
            result = changeEmailUsecase.generateTokenForEmailChange(request);
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        if (result == null)
            return ApiResponse.failedResponse("Email verificaiton failed : " + errorMessage);
        return ApiResponse.successfulResponse("Email verified successfully.", result);
    }

    @PostMapping("/verify-change-email-token")
    public ResponseEntity<?> verifyEmailChange(@RequestBody ChangeEmailVerifyRequestDTO request) {
        ChangeEmailVerifyResponseDTO result = null;
        String errorMessage = "";
        try {
            result = changeEmailUsecase.verifyToken(request);
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        if (result == null)
            return ApiResponse.failedResponse("Email verificaiton failed : " + errorMessage);
        return ApiResponse.successfulResponse("Email verified successfully.", result);
    }
}
