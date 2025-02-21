package com.warehub.warehub.infrastructure.customerOrders.controller;

import com.warehub.warehub.common.response.ApiResponse;
import com.warehub.warehub.infrastructure.customerOrders.dto.ConfirmOrderRequestDTO;
import com.warehub.warehub.infrastructure.customerOrders.dto.CustomerOrderDetailRequestDTO;
import com.warehub.warehub.infrastructure.customerOrders.dto.PaginatedCustomerOrderRequestDTO;
import com.warehub.warehub.infrastructure.security.Claims;
import com.warehub.warehub.usecase.customerOrder.CustomerOrderUsecase;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@RestController
@RequestMapping("/api/v1/orders")
public class CustomerOrderController {

    private final CustomerOrderUsecase customerOrderUsecase;

    public CustomerOrderController(CustomerOrderUsecase customerOrderUsecase) {
        this.customerOrderUsecase = customerOrderUsecase;
    }

    @GetMapping
    public ResponseEntity<?> getCustomerOrders(@RequestParam int page,
                                               @RequestParam int limit,
                                               @RequestParam(required = false) Long customerOrderStatusId,
                                               @RequestParam(required = false) String search,
                                               @RequestParam(required = false)  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDate,
                                               @RequestParam(required = false)  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDate
    ) {
        Long userId = Claims.getUserIdFromJwt();

        PaginatedCustomerOrderRequestDTO reqDTO = new PaginatedCustomerOrderRequestDTO();
        reqDTO.setUserId(userId);
        reqDTO.setCustomerOrderStatusId(customerOrderStatusId);
        reqDTO.setPage(page);
        reqDTO.setLimit(limit);
        reqDTO.setSearchQuery((search != null && !search.isBlank()) ? search : null);
        reqDTO.setStartDate(startDate != null ? startDate.withOffsetSameInstant(ZoneOffset.UTC) : null);
        reqDTO.setEndDate(endDate != null ? endDate.withOffsetSameInstant(ZoneOffset.UTC) : null);

        return ApiResponse.successfulResponse("Get all customer orders success", customerOrderUsecase.getAllCustomerOrders(reqDTO));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getCustomerOrder(@PathVariable Long orderId) {

        Long userId = Claims.getUserIdFromJwt();

        CustomerOrderDetailRequestDTO requestDTO = new CustomerOrderDetailRequestDTO();
        requestDTO.setUserId(userId);
        requestDTO.setOrderId(orderId);

        return ApiResponse.successfulResponse("Get customer order detail success", customerOrderUsecase.getCustomerOrder(requestDTO));
    }

    @PostMapping("/{orderId}")
    public ResponseEntity<?> confirmCustomerOrder(@PathVariable Long orderId) {

        Long userId = Claims.getUserIdFromJwt();

        ConfirmOrderRequestDTO requestDTO = new ConfirmOrderRequestDTO();
        requestDTO.setUserId(userId);
        requestDTO.setOrderId(orderId);

        return ApiResponse.successfulResponse("Confirm order success", customerOrderUsecase.confirmCustomerOrder(requestDTO));
    }
}
