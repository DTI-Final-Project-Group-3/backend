package com.warehub.warehub.infrastructure.customerOrders.controller;

import com.warehub.warehub.common.response.ApiResponse;
import com.warehub.warehub.infrastructure.customerOrders.dto.PaginatedCustomerOrderRequestDTO;
import com.warehub.warehub.infrastructure.users.dto.UserAuth;
import com.warehub.warehub.infrastructure.users.repository.UsersRepository;
import com.warehub.warehub.usecase.customerOrder.CustomerOrderUsecase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/api/v1/orders")
public class CustomerOrderController {

    private final CustomerOrderUsecase customerOrderUsecase;
    private final UsersRepository usersRepository;

    public CustomerOrderController(CustomerOrderUsecase customerOrderUsecase, UsersRepository usersRepository) {
        this.customerOrderUsecase = customerOrderUsecase;
        this.usersRepository = usersRepository;
    }

    @GetMapping
    public ResponseEntity<?> getCustomerOrders(@RequestParam int page,
                                               @RequestParam int limit,
                                               @RequestParam(required = false) Long customerOrderStatusId,
                                               @RequestParam(required = false) String search) {

        Long userId = Objects.requireNonNull(UserAuth.getCurrentUser(usersRepository)).getId();

        PaginatedCustomerOrderRequestDTO reqDTO = new PaginatedCustomerOrderRequestDTO();
        reqDTO.setUserId(userId);
        reqDTO.setCustomerOrderStatusId(customerOrderStatusId);
        reqDTO.setPage(page);
        reqDTO.setLimit(limit);
        reqDTO.setSearchQuery(search);

        return ApiResponse.successfulResponse("Get all customer orders success", customerOrderUsecase.getAllCustomerOrders(reqDTO));
    }
}
