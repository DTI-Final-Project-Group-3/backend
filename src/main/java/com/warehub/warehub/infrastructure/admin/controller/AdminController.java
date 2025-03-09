package com.warehub.warehub.infrastructure.admin.controller;

import com.warehub.warehub.common.response.ApiResponse;
import com.warehub.warehub.entity.enums.RoleType;
import com.warehub.warehub.infrastructure.admin.dto.AssignWarehouseRequestDTO;
import com.warehub.warehub.infrastructure.admin.dto.AssignWarehouseResponseDTO;
import com.warehub.warehub.infrastructure.admin.dto.UserAdminDetailResponseDTO;
import com.warehub.warehub.infrastructure.signup.dto.CreateUserRequestDTO;
import com.warehub.warehub.infrastructure.users.dto.*;
import com.warehub.warehub.usecase.admin.AdminUsecase;
import com.warehub.warehub.usecase.signup.CreateUserUsecase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
            return ApiResponse.failedResponse("Failed to create admin or this email already exists : " + errorMessage);
        return ApiResponse.successfulResponse("Admin created successfully.", result);
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<?> deleteAdmin(@PathVariable("id") Long id) {
        String result = null;
        String errorMessage = "";
        try {
            result = adminUsecase.deleteAdmin(id);
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        if (result == null)
            return ApiResponse.failedResponse("Failed to delete user : " + errorMessage);
        return ApiResponse.successfulResponse("User deleted successfully.", result);
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

    @GetMapping("/current-warehouse")
    public ResponseEntity<?> getCurrentWarehouse() {
        CurrentWarehouseResponseDTO result = null;
        String errorMessage = "";
        try {
            result = adminUsecase.getCurrentWarehouseDTO();
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        if (result == null)
            return ApiResponse.failedResponse("Failed to get current warehouse assignment :  " + errorMessage);
        return ApiResponse.successfulResponse("Success get current warehouse assignment ", result);
    }

}
