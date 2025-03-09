package com.warehub.warehub.infrastructure.signup.controller;

import com.warehub.warehub.common.response.ApiResponse;
import com.warehub.warehub.infrastructure.signup.dto.CreateUserRequestDTO;
import com.warehub.warehub.infrastructure.signup.dto.EmailVerificationVerifyRequestDTO;
import com.warehub.warehub.infrastructure.signup.dto.EmailVerificationVerifyResponseDTO;
import com.warehub.warehub.infrastructure.users.dto.UserDetailResponseDTO;
import com.warehub.warehub.infrastructure.users.repository.RolesRepository;
import com.warehub.warehub.usecase.signup.CreateUserUsecase;
import com.warehub.warehub.usecase.signup.EmailVerificationUsecase;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/signup")
public class SignupController {
    private final CreateUserUsecase createUserUsecase;
    private final EmailVerificationUsecase emailVerificationUsecase;

    public SignupController(CreateUserUsecase createUserUsecase,
            EmailVerificationUsecase emailVerificationUsecase) {
        this.createUserUsecase = createUserUsecase;
        this.emailVerificationUsecase = emailVerificationUsecase;
    }

    @PostMapping
    public ResponseEntity<?> createUserCustomer(@Valid @RequestBody CreateUserRequestDTO req,
                                                @RequestParam(defaultValue = "NOT_VERIFIED") String role) {
        UserDetailResponseDTO result = null;
        String errorMessage = "";
        try {
            result = createUserUsecase.createUser(req, role);
            //emailVerificationUsecase.send(result.getId());
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        if (result == null)
            return ApiResponse.failedResponse("Failed to create user or this email already exists : " + errorMessage);
        return ApiResponse.successfulResponse("User created successfully. Please check your email to verify your account.", result);
    }

    @Autowired
    private RolesRepository rolesRepository;

    @PostMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestBody EmailVerificationVerifyRequestDTO request) {
        EmailVerificationVerifyResponseDTO result = null;
        String errorMessage = "";
        try {
            result = emailVerificationUsecase.verifyEmailVerificationToken(request);
            //emailVerificationUsecase.send(result.getId());
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        if (result == null)
            return ApiResponse.failedResponse("Email verificaiton failed : " + errorMessage);
        return ApiResponse.successfulResponse("Email verified successfully.", result);
    }
}
