package com.warehub.warehub.infrastructure.users.controller;


import com.warehub.warehub.common.response.ApiResponse;
import com.warehub.warehub.infrastructure.users.dto.*;
import com.warehub.warehub.usecase.user.GoogleLoginUsecase;
import com.warehub.warehub.usecase.user.LoginUsecase;
import com.warehub.warehub.usecase.user.ResetPasswordUsecase;
import com.warehub.warehub.usecase.user.TokenGenerationUsecase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final LoginUsecase loginUsecase;
    private final TokenGenerationUsecase tokenGenerationUsecase;

    @Autowired
    private GoogleLoginUsecase googleLoginUsecase;

    @Autowired
    private ResetPasswordUsecase resetPasswordUsecase;

    public AuthController(LoginUsecase loginUsecase, TokenGenerationUsecase tokenGenerationUsecase) {
        this.loginUsecase = loginUsecase;
        this.tokenGenerationUsecase = tokenGenerationUsecase;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated @RequestBody LoginRequestDTO req) {
        LoginResponseDTO result = null;
        String errorMessage = "";
        try {
            result = loginUsecase.authenticateUser(req);
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        if (result == null)
            return ApiResponse.failedResponse("Login failed : "+errorMessage);
        return ApiResponse.successfulResponse("Login successful", result);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // will implement it later
        return ApiResponse.successfulResponse("Logout successful", null);
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String token) {
        // Step 1: Verify token format
        if (token == null || !token.startsWith("Bearer ")) {
            return ApiResponse.failedResponse("Invalid token format");
        }

        // Step 2: Extract the refresh token
        String refreshToken = token.substring(7); // Remove "Bearer " prefix

        // Step 3: Verify the refresh token
        if (!tokenGenerationUsecase.tokenVerify(refreshToken)) {
            return ApiResponse.failedResponse("Token is expired or invalid");
        }

        // Step 4: Call the refreshToken method from LoginUsecase
        LoginResponseDTO response = null;
        String errorMessage = "";
        try {
            response = loginUsecase.refreshToken(refreshToken);  // Call the refreshToken method
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }

        // Step 5: Handle failure
        if (response == null) {
            return ApiResponse.failedResponse("Failed to refresh token: " + errorMessage);
        }
        System.out.println("Token refreshed");
        // Step 6: Return the successful response with new tokens
        return ApiResponse.successfulResponse("Successfully refreshed token", response);
    }

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody GoogleLoginRequestDTO request) {
        LoginResponseDTO result = null;
        String errorMessage = "";
        try {
            result = googleLoginUsecase.login(request);
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        if (result == null)
            return ApiResponse.failedResponse("Google Login failed : "+errorMessage);
        return ApiResponse.successfulResponse("GoogleLogin successful", result);
    }

    @PostMapping("/reset-password-request")
    public ResponseEntity<?> resetPasswordRequest() {
        ResetPasswordGenerateResponseDTO result = null;
        String errorMessage = "";
        try {
            result = resetPasswordUsecase.generateToken();
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        if (result == null)
            return ApiResponse.failedResponse("Reset password request failed : "+errorMessage);
        return ApiResponse.successfulResponse("Reset password request successful", result);
    }

    @PostMapping("/reset-password-verify")
    public ResponseEntity<?> resetPasswordVerify(@RequestBody  ResetPasswordVerifyRequestDTO requestDTO) {
        ResetPasswordVerifyResponseDTO result = null;
        String errorMessage = "";
        try {
            result = resetPasswordUsecase.verifyToken(requestDTO);
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        if (result == null)
            return ApiResponse.failedResponse("Reset password request failed : "+errorMessage);
        return ApiResponse.successfulResponse("Reset password request successful", result);
    }
}