package com.warehub.warehub.usecase.customerOrder.impl;

import com.warehub.warehub.common.exceptions.DataNotFoundException;
import com.warehub.warehub.common.utils.PaginationInfo;
import com.warehub.warehub.entity.CustomerOrder;
import com.warehub.warehub.infrastructure.customerOrders.dto.CustomerOrderResponseDTO;
import com.warehub.warehub.infrastructure.customerOrders.dto.PaginatedCustomerOrderRequestDTO;
import com.warehub.warehub.infrastructure.customerOrders.repository.CustomerOrderRepository;
import com.warehub.warehub.usecase.customerOrder.CustomerOrderUsecase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerOrderUsecaseImpl implements CustomerOrderUsecase {
    private final CustomerOrderRepository customerOrderRepository;

    public CustomerOrderUsecaseImpl(CustomerOrderRepository customerOrderRepository) {
        this.customerOrderRepository = customerOrderRepository;
    }

    @Override
    public PaginationInfo<CustomerOrderResponseDTO> getAllCustomerOrders(PaginatedCustomerOrderRequestDTO request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getLimit());

        Page<CustomerOrder> customerOrderPage = customerOrderRepository.findAllByFilters(
                request.getUserId(),
                Optional.ofNullable(request.getCustomerOrderStatusId()).orElseThrow(() -> new DataNotFoundException("Order status not found")),
                Optional.ofNullable(request.getSearchQuery()).orElse(""),
                pageable
        );

        List<CustomerOrderResponseDTO> orderResponses = customerOrderPage.getContent()
                .stream()
                .map(CustomerOrderResponseDTO::toEntity)
                .toList();

        return new PaginationInfo<>(customerOrderPage, orderResponses);
    }
}
