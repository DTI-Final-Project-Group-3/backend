package com.warehub.warehub.infrastructure.users.controller;

import com.warehub.warehub.common.response.ApiResponse;
import com.warehub.warehub.entity.enums.RoleType;
import com.warehub.warehub.infrastructure.users.dto.*;
import com.warehub.warehub.usecase.user.AdminUsecase;
import com.warehub.warehub.usecase.user.CreateUserUsecase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user/admin")
public class AdminController {
    @Autowired
    private AdminUsecase adminUsecase;

    @Autowired
    private CreateUserUsecase createUserUsecase;

    @GetMapping
    private ResponseEntity<?> getAllAdmin() {
        List<UserAdminDetailResponseDTO> result = null;
        String errorMessage = "";
        try {
            result = adminUsecase.getAllAdminWarehouse();
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        if (result == null)
            return ApiResponse.failedResponse("Failed to get admin users : " + errorMessage);
        return ApiResponse.successfulResponse("Get admin users successful", result);
    }

    @GetMapping("/assigned")
    private ResponseEntity<?> getAllAdminAssigned(@RequestParam Long warehouseId) {
        List<UserAdminDetailResponseDTO> result = null;
        String errorMessage = "";

        if ((warehouseId == null) || (warehouseId <= 0)) {
            return ApiResponse.failedResponse("Need warehouseId parameter");
        }

        try {
            result = adminUsecase.getAllAdminWarehouseAssigned(warehouseId);
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        if (result == null)
            return ApiResponse.failedResponse("Failed to get admin users not assigned : " + errorMessage);
        return ApiResponse.successfulResponse("Get admin users not assigned successful", result);
    }

    @GetMapping("/not-assigned")
    private ResponseEntity<?> getAllAdminNotAssigned() {
        List<UserAdminDetailResponseDTO> result = null;
        String errorMessage = "";
        try {
            result = adminUsecase.getAllAdminWarehouseNotAssigned();
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        if (result == null)
            return ApiResponse.failedResponse("Failed to get admin users not assigned : " + errorMessage);
        return ApiResponse.successfulResponse("Get admin users not assigned successful", result);
    }

    @PostMapping
    public ResponseEntity<?> createUserAdmin(@RequestBody CreateUserRequestDTO req) {
        UserDetailResponseDTO result = null;
        String errorMessage = "";
        try {
            result = createUserUsecase.createUser(req, RoleType.ADMIN_WAREHOUSE.toString());
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        if (result == null)
            return ApiResponse.failedResponse("Failed to create user or this email already exists : " + errorMessage);
        return ApiResponse.successfulResponse("User created successfully. Please check your email to verify your account.", result);
    }

    @PostMapping("/assign-warehouse")
    public ResponseEntity<?> assignWarehouse(@RequestBody AssignWarehouseRequestDTO req) {
        AssignWarehouseResponseDTO result = null;
        String errorMessage = "";
        try {
            result = adminUsecase.assignWarehouse(req);
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        if (result == null)
            return ApiResponse.failedResponse("Failed to assign warehouse : " + errorMessage);
        return ApiResponse.successfulResponse("Success assign warehouse.", result);
    }

    @DeleteMapping("/assign-warehouse")
    public ResponseEntity<?> removeWarehouseAssignment(@RequestBody AssignWarehouseRequestDTO req) {
        AssignWarehouseResponseDTO result = null;
        String errorMessage = "";
        try {
            result = adminUsecase.removeWarehouseAssignment(req);
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        if (result == null)
            return ApiResponse.failedResponse("Failed to delete assignent warehouse :  " + errorMessage);
        return ApiResponse.successfulResponse("Success delete assignent warehouse.", result);
    }
}
