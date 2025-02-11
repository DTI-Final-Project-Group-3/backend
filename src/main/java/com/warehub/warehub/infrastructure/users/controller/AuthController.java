package com.warehub.warehub.infrastructure.users.controller;


import com.warehub.warehub.common.response.ApiResponse;
import com.warehub.warehub.infrastructure.users.dto.LoginRequestDTO;
import com.warehub.warehub.infrastructure.users.dto.LoginResponseDTO;
import com.warehub.warehub.usecase.user.LoginUsecase;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final LoginUsecase loginUsecase;

    public AuthController(LoginUsecase loginUsecase) {
        this.loginUsecase = loginUsecase;
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
}