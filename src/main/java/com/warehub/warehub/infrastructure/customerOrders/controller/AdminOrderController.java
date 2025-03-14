package com.warehub.warehub.infrastructure.customerOrders.controller;

import com.warehub.warehub.common.response.ApiResponse;
import com.warehub.warehub.infrastructure.customerOrders.dto.UpdateOrderRequestDTO;
import com.warehub.warehub.infrastructure.security.Claims;
import com.warehub.warehub.usecase.customerOrder.AdminCustomerOrderUsecase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/orders")
public class AdminOrderController {
    private final AdminCustomerOrderUsecase adminCustomerOrderUsecase;

    public AdminOrderController(AdminCustomerOrderUsecase adminCustomerOrderUsecase) {
        this.adminCustomerOrderUsecase = adminCustomerOrderUsecase;
    }

    @PutMapping("/{orderId}/order-approval")
    public ResponseEntity<?> updateCustomerOrderStatus(@PathVariable Long orderId,
                                                       @RequestBody UpdateOrderRequestDTO requestDTO) {
        Long userId = Claims.getUserIdFromJwt();
        requestDTO.setUserId(userId);
        requestDTO.setOrderId(orderId);

        return ApiResponse.successfulResponse("Successfully modified customer order status",
                adminCustomerOrderUsecase.updateCustomerOrder(requestDTO));
    }

    @PutMapping("/{orderId}/approve-payment")
    public ResponseEntity<?> approveCustomerOrderPayment(@PathVariable Long orderId,
                                                         @RequestBody UpdateOrderRequestDTO requestDTO) {
        Long userId = Claims.getUserIdFromJwt();
        requestDTO.setUserId(userId);
        requestDTO.setOrderId(orderId);

        return ApiResponse.successfulResponse("Successfully approve manual payment for order ID : " +requestDTO.getOrderId(),
                adminCustomerOrderUsecase.updateCustomerOrder(requestDTO));
    }

    @PutMapping("/{orderId}/send-order")
    public ResponseEntity<?> sendCustomerOrder(@PathVariable Long orderId,
                                               @RequestBody UpdateOrderRequestDTO requestDTO) {
        Long userId = Claims.getUserIdFromJwt();
        requestDTO.setUserId(userId);
        requestDTO.setOrderId(orderId);

        return ApiResponse.successfulResponse("Successfully send order items for order ID : " +requestDTO.getOrderId(),
                adminCustomerOrderUsecase.processCustomerOrder(requestDTO));
    }
}
