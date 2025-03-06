package com.warehub.warehub.infrastructure.users.controller;

import com.warehub.warehub.common.response.ApiResponse;
import com.warehub.warehub.infrastructure.users.dto.UserAddressRequestDTO;
import com.warehub.warehub.infrastructure.users.dto.UserAddressResponseDTO;
import com.warehub.warehub.infrastructure.users.repository.UserAddressRepository;
import com.warehub.warehub.usecase.user.UserAddressUsecase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user/address")
public class UserAddressController {

    @Autowired
    private UserAddressRepository userAddressRepository;

    @Autowired
    private UserAddressUsecase userAddressUsecase;

    @GetMapping
    private ResponseEntity<?> getAll() {
        List<UserAddressResponseDTO> result = null;
        String errorMessage = "";
        try {
            result = userAddressUsecase.getAll();
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        if (result == null)
            return ApiResponse.failedResponse("Failed to get all address : " + errorMessage);
        return ApiResponse.successfulResponse("Get all address successful", result);
    }

    @PostMapping
    private ResponseEntity<?> create(@RequestBody UserAddressRequestDTO request) {
        UserAddressResponseDTO result = null;
        String errorMessage = "";
        try {
            result = userAddressUsecase.create(request);
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        if (result == null)
            return ApiResponse.failedResponse("Failed to create a new address : " + errorMessage);
        return ApiResponse.successfulResponse("Create new address successful", result);
    }

    @GetMapping("/main")
    private ResponseEntity<?> getMain() {
        UserAddressResponseDTO result = null;
        String errorMessage = "";
        try {
            result = userAddressUsecase.getMain();
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        if (result == null)
            return ApiResponse.failedResponse("Failed to get main address : " + errorMessage);
        return ApiResponse.successfulResponse("Get main address successful", result);
    }

    @GetMapping("/id/{id}")
    private ResponseEntity<?> getById(@PathVariable("id") Long id) {
        UserAddressResponseDTO result = null;
        String errorMessage = "";
        try {
            result = userAddressUsecase.read(id);
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        if (result == null)
            return ApiResponse.failedResponse("Failed to get address by ID : " + errorMessage);
        return ApiResponse.successfulResponse("Get address by ID successful", result);
    }

    @PutMapping("/id/{id}")
    private ResponseEntity<?> updateById(@PathVariable("id") Long id, @RequestBody UserAddressRequestDTO request) {
        UserAddressResponseDTO result = null;
        String errorMessage = "";
        try {
            result = userAddressUsecase.update(id,request);
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        if (result == null)
            return ApiResponse.failedResponse("Failed to update address by ID : " + errorMessage);
        return ApiResponse.successfulResponse("Update address by ID successful", result);
    }

    @DeleteMapping("/id/{id}")
    private ResponseEntity<?> deleteById(@PathVariable("id") Long id) {
        UserAddressResponseDTO result = null;
        String errorMessage = "";
        try {
            result = userAddressUsecase.delete(id);
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        if (result == null)
            return ApiResponse.failedResponse("Failed to delete address by ID : " + errorMessage);
        return ApiResponse.successfulResponse("Delete address by ID successful", result);
    }
}
