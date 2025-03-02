package com.warehub.warehub.infrastructure.customerOrders.controller;

import com.warehub.warehub.common.response.ApiResponse;
import com.warehub.warehub.infrastructure.customerOrders.dto.UpdateOrderRequestDTO;
import com.warehub.warehub.infrastructure.security.Claims;
import com.warehub.warehub.usecase.customerOrder.AdminCustomerOrderUsecase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/orders")
public class AdminOrderController {
    private final AdminCustomerOrderUsecase adminCustomerOrderUsecase;

    public AdminOrderController(AdminCustomerOrderUsecase adminCustomerOrderUsecase) {
        this.adminCustomerOrderUsecase = adminCustomerOrderUsecase;
    }

    // TODO 1 - Approve or decline customer orders by their payment proof image:
    // check customer order payment image proof (if using manual)
    // while customer order createdAt < createdAt + 1 hr
    // if more than 1hr from createdAt automatically canceled the order
    // if payment approved, change the order status to (order processed)
    @PutMapping("/{orderId}/order-approval")
    public ResponseEntity<?> updateCustomerOrderStatus(UpdateOrderRequestDTO requestDTO) {
        Long userId = Claims.getUserIdFromJwt();

        UpdateOrderRequestDTO updateOrderRequestDTO = new UpdateOrderRequestDTO();
        updateOrderRequestDTO.setUserId(userId);
        updateOrderRequestDTO.setOrderId(requestDTO.getOrderId());

        return ApiResponse.successfulResponse("Successfully modified customer order status",
                adminCustomerOrderUsecase.updateCustomerOrder(updateOrderRequestDTO));
    }
}
