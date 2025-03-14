package com.warehub.warehub.infrastructure.customerOrders.controller;

import com.warehub.warehub.common.response.ApiResponse;
import com.warehub.warehub.infrastructure.customerOrders.dto.ConfirmOrderRequestDTO;
import com.warehub.warehub.infrastructure.customerOrders.dto.CustomerOrderDetailRequestDTO;
import com.warehub.warehub.infrastructure.customerOrders.dto.CustomerOrderHistoryRequestDTO;
import com.warehub.warehub.infrastructure.customerOrders.dto.PaginatedCustomerOrderRequestDTO;
import com.warehub.warehub.infrastructure.security.Claims;
import com.warehub.warehub.usecase.customerOrder.CustomerOrderUsecase;
import com.warehub.warehub.usecase.transaction.ManualTransactionUsecase;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@RestController
@RequestMapping("/api/v1/orders")
public class CustomerOrderController {

    private final CustomerOrderUsecase customerOrderUsecase;
    private final ManualTransactionUsecase manualTransactionUsecase;

    public CustomerOrderController(CustomerOrderUsecase customerOrderUsecase,
                                   ManualTransactionUsecase manualTransactionUsecase
    ) {
        this.customerOrderUsecase = customerOrderUsecase;
        this.manualTransactionUsecase = manualTransactionUsecase;
    }

    @GetMapping
    public ResponseEntity<?> getCustomerOrders(@RequestParam int page,
                                               @RequestParam int limit,
                                               @RequestParam(required = false) Long customerOrderStatusId,
                                               @RequestParam(required = false) Long warehouseId,
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
        reqDTO.setWarehouseId(warehouseId);

        System.out.println("Start Date : "+reqDTO.getStartDate());
        System.out.println("End Date : "+reqDTO.getEndDate());

        return ApiResponse.successfulResponse("Get all customer orders success", customerOrderUsecase.getAllCustomerOrders(reqDTO));
    }

    @GetMapping("/history")
    public ResponseEntity<?> getHistoryCustomerOrder(@RequestParam int page,
                                                     @RequestParam int limit,
                                                     @RequestParam(required = false)  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                     @RequestParam(required = false)  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                     @RequestParam(required = false) Long warehouseId,
                                                     @RequestParam(required = false) Long customerOrderStatusId,
                                                     @RequestParam(required = false) Long productId,
                                                     @RequestParam(required = false) Long productCategoryId
                                                     ){
        CustomerOrderHistoryRequestDTO requestDTO = new CustomerOrderHistoryRequestDTO(page, limit, startDate, endDate, warehouseId, customerOrderStatusId, productId, productCategoryId);
        return ApiResponse.successfulResponse("Get history customer orders success", customerOrderUsecase.getHistoryCustomerOrder(requestDTO));
    }

    @GetMapping("/daily")
    public ResponseEntity<?> getTotalDailyCustomerOrder(@RequestParam(required = false)  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                     @RequestParam(required = false)  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                     @RequestParam(required = false) Long warehouseId,
                                                     @RequestParam(required = false) Long customerOrderStatusId,
                                                     @RequestParam(required = false) Long productId,
                                                     @RequestParam(required = false) Long productCategoryId
    ){

        CustomerOrderHistoryRequestDTO requestDTO = new CustomerOrderHistoryRequestDTO(startDate, endDate, warehouseId, customerOrderStatusId, productId, productCategoryId);
        return ApiResponse.successfulResponse("Get history customer orders success", customerOrderUsecase.getDailyTotalCustomerOrder(requestDTO));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getCustomerOrder(@PathVariable Long orderId) {

        Long userId = Claims.getUserIdFromJwt();

        CustomerOrderDetailRequestDTO requestDTO = new CustomerOrderDetailRequestDTO();
        requestDTO.setUserId(userId);
        requestDTO.setOrderId(orderId);

        return ApiResponse.successfulResponse("Get customer order detail success", customerOrderUsecase.getCustomerOrder(requestDTO));
    }

    @PostMapping("/customer/{orderId}")
    public ResponseEntity<?> confirmCustomerOrder(@PathVariable Long orderId) {

        Long userId = Claims.getUserIdFromJwt();

        ConfirmOrderRequestDTO requestDTO = new ConfirmOrderRequestDTO();
        requestDTO.setUserId(userId);
        requestDTO.setOrderId(orderId);

        return ApiResponse.successfulResponse("Confirm order success", customerOrderUsecase.confirmCustomerOrder(requestDTO));
    }

    @PutMapping("/customer/cancel/{orderId}")
    public ResponseEntity<?> cancelCustomerOrder(@PathVariable Long orderId) {
        return ApiResponse.successfulResponse("Cancel order with ID : " + orderId + " success", customerOrderUsecase.cancelCustomerOrder(orderId));
    }
}
