package com.warehub.warehub.infrastructure.users.controller;

import com.warehub.warehub.common.response.ApiResponse;
import com.warehub.warehub.entity.User;
import com.warehub.warehub.infrastructure.users.dto.UserDetailRequestDTO;
import com.warehub.warehub.infrastructure.users.dto.UserDetailResponseDTO;
import com.warehub.warehub.infrastructure.users.repository.UsersRepository;
import com.warehub.warehub.usecase.user.UserDetailUsecase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private UsersRepository usersRepository;

    private final UserDetailUsecase userDetailUsecase;

    public UserController(UserDetailUsecase userDetailUsecase) {
        this.userDetailUsecase = userDetailUsecase;
    }

    @GetMapping("/detail")
    public ResponseEntity<?> getUserDetail(JwtAuthenticationToken authToken) {
        UserDetailResponseDTO responseDTO;
        try {
            responseDTO = userDetailUsecase.getUserDetail(authToken);
        } catch (Exception e) {
            return ApiResponse.failedResponse("Failed to get user detail, " + e.getMessage());
        }
        return ApiResponse.successfulResponse("Get user detail successful", responseDTO);
    }

    @PutMapping("/detail")
    public ResponseEntity<?> getUserDetail(JwtAuthenticationToken authToken, @RequestBody UserDetailRequestDTO req) {
        UserDetailResponseDTO responseDTO;
        try {
            responseDTO = userDetailUsecase.updateUserDetail(authToken, req);
        } catch (Exception e) {
            return ApiResponse.failedResponse("Failed to update user detail, " + e.getMessage());
        }
        return ApiResponse.successfulResponse("Update user detail successful", responseDTO);
    }
}
