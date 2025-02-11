package com.warehub.warehub.infrastructure.users.controller;

import com.warehub.warehub.common.response.ApiResponse;
import com.warehub.warehub.entity.EmailVerificationToken;
import com.warehub.warehub.entity.User;
import com.warehub.warehub.entity.enums.RoleType;
import com.warehub.warehub.infrastructure.users.dto.CreateUserRequestDTO;
import com.warehub.warehub.infrastructure.users.dto.UserDetailResponseDTO;
import com.warehub.warehub.infrastructure.users.repository.EmailVerificationTokenRepository;
import com.warehub.warehub.infrastructure.users.repository.RolesRepository;
import com.warehub.warehub.infrastructure.users.repository.UsersRepository;
import com.warehub.warehub.usecase.user.CreateUserUsecase;
import com.warehub.warehub.usecase.user.EmailVerificationUsecase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.Optional;

import static com.warehub.warehub.entity.enums.RoleUtil.roleEnumFromString;

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
    public ResponseEntity<?> createUserCustomer(@RequestBody CreateUserRequestDTO req,
                                                @RequestParam(defaultValue = "NOT_VERIFIED") String role) {
        UserDetailResponseDTO result;
        RoleType roleType = roleEnumFromString(role, RoleType.NOT_VERIFIED);
        try {
            result = createUserUsecase.createUser(req, roleType);
            emailVerificationUsecase.send(result.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.failedResponse("Failed to create user or this email already exists");
        }
        return ApiResponse.successfulResponse("User created successfully. Please check your email to verify your account.", result);
    }

    @Autowired
    private RolesRepository rolesRepository;

    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        return emailVerificationUsecase.verify(token);
    }
}
