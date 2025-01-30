package com.warehub.warehub.infrastructure.users.controller;

import com.warehub.warehub.common.response.ApiResponse;
import com.warehub.warehub.infrastructure.users.dto.LoginRequestDTO;
import com.warehub.warehub.infrastructure.users.dto.UserDetailResponseDTO;
import com.warehub.warehub.usecase.user.CreateUserUsecase;
import com.warehub.warehub.usecase.user.GetUserDetailUsecase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final GetUserDetailUsecase getUserDetailUsecase;

    public UserController(GetUserDetailUsecase getUserDetailUsecase) {
        this.getUserDetailUsecase = getUserDetailUsecase;
    }

    @GetMapping("/detail")
    public ResponseEntity<?> getUserDetail(JwtAuthenticationToken authToken) {
        UserDetailResponseDTO responseDTO;
        try {
            responseDTO = getUserDetailUsecase.getUserDetail(authToken);
        } catch (Exception e) {
            return ApiResponse.failedResponse("Failed to get user detail, " + e.getMessage());
        }
        return ApiResponse.successfulResponse("Get user detail successful", responseDTO);
    }
}
