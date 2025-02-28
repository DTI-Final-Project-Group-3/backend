package com.warehub.warehub.infrastructure.users.controller;

import com.warehub.warehub.common.response.ApiResponse;
import com.warehub.warehub.infrastructure.users.dto.UserDetailRequestDTO;
import com.warehub.warehub.infrastructure.users.dto.UserDetailResponseDTO;
import com.warehub.warehub.infrastructure.users.repository.UsersRepository;
import com.warehub.warehub.usecase.user.UserDetailUsecase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/detail")
public class UserDetailController {

    private final UserDetailUsecase userDetailUsecase;

    public UserDetailController(UserDetailUsecase userDetailUsecase) {
        this.userDetailUsecase = userDetailUsecase;
    }

    @GetMapping
    public ResponseEntity<?> getUserDetail(JwtAuthenticationToken authToken) {
        UserDetailResponseDTO responseDTO = null;
        String errorMessage = "";
        try {
            responseDTO = userDetailUsecase.getUserDetail(authToken);
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        if (responseDTO == null)
            return ApiResponse.failedResponse("Failed to get user detail : " + errorMessage);
        return ApiResponse.successfulResponse("Get user detail successful", responseDTO);
    }

    @PutMapping
    public ResponseEntity<?> getUserDetail(JwtAuthenticationToken authToken, @RequestBody UserDetailRequestDTO req) {
        UserDetailResponseDTO responseDTO = null;
        String errorMessage = "";
        try {
            responseDTO = userDetailUsecase.updateUserDetail(authToken, req);
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        if (responseDTO == null)
            return ApiResponse.failedResponse("Failed to update user detail : " + errorMessage);
        return ApiResponse.successfulResponse("Update user detail successful", responseDTO);
    }
}
