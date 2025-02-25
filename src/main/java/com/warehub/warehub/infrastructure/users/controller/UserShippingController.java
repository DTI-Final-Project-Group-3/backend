package com.warehub.warehub.infrastructure.users.controller;

import com.warehub.warehub.common.response.ApiResponse;
import com.warehub.warehub.infrastructure.users.dto.ShippingCostRequestDTO;
import com.warehub.warehub.infrastructure.users.dto.ShippingCostResponseDTO;
import com.warehub.warehub.infrastructure.users.dto.UserDetailResponseDTO;
import com.warehub.warehub.usecase.user.ShippingUsecase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/shipping")
public class UserShippingController {

    @Autowired
    private ShippingUsecase shippingUsecase;

    @PostMapping("/cost")
    public ResponseEntity<?> getShippingCost(@RequestBody ShippingCostRequestDTO requestDTO) {
        ShippingCostResponseDTO responseDTO = null;
        String errorMessage = "";
        try {
            responseDTO = shippingUsecase.getCost(requestDTO);
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        if (responseDTO == null)
            return ApiResponse.failedResponse("Failed to get shipping cost : " + errorMessage);
        return ApiResponse.successfulResponse("Get shipping cost successful", responseDTO);
    }

    @PostMapping("/cost-dummy")
    public ResponseEntity<?> getShippingCostDummy(@RequestBody ShippingCostRequestDTO requestDTO) {
        ShippingCostResponseDTO responseDTO = null;
        String errorMessage = "";
        try {
            responseDTO = shippingUsecase.getCostDummy(requestDTO);
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        if (responseDTO == null)
            return ApiResponse.failedResponse("Failed to get shipping cost : " + errorMessage);
        return ApiResponse.successfulResponse("Get shipping cost successful", responseDTO);
    }
}
