package com.warehub.warehub.usecase.customerOrder.impl;

import com.warehub.warehub.common.exceptions.DataNotFoundException;
import com.warehub.warehub.common.utils.PaginationInfo;
import com.warehub.warehub.entity.CustomerOrder;
import com.warehub.warehub.entity.CustomerOrderStatus;
import com.warehub.warehub.entity.User;
import com.warehub.warehub.infrastructure.customerOrderStatus.CustomerOrderStatusRepository;
import com.warehub.warehub.infrastructure.customerOrders.dto.ConfirmOrderRequestDTO;
import com.warehub.warehub.infrastructure.customerOrders.dto.CustomerOrderResponseDTO;
import com.warehub.warehub.infrastructure.customerOrders.dto.CustomerOrderDetailRequestDTO;
import com.warehub.warehub.infrastructure.customerOrders.dto.PaginatedCustomerOrderRequestDTO;
import com.warehub.warehub.infrastructure.customerOrders.repository.CustomerOrderRepository;
import com.warehub.warehub.infrastructure.customerOrders.specification.CustomerOrderSpecification;
import com.warehub.warehub.infrastructure.users.repository.UsersRepository;
import com.warehub.warehub.usecase.customerOrder.CustomerOrderUsecase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerOrderUsecaseImpl implements CustomerOrderUsecase {
    private final CustomerOrderRepository customerOrderRepository;
    private final UsersRepository usersRepository;
    private final CustomerOrderStatusRepository customerOrderStatusRepository;

    public CustomerOrderUsecaseImpl(CustomerOrderRepository customerOrderRepository,
                                    UsersRepository usersRepository,
                                    CustomerOrderStatusRepository customerOrderStatusRepository
    ) {
        this.customerOrderRepository = customerOrderRepository;
        this.usersRepository = usersRepository;
        this.customerOrderStatusRepository = customerOrderStatusRepository;
    }

    @Override
    public PaginationInfo<CustomerOrderResponseDTO> getAllCustomerOrders(PaginatedCustomerOrderRequestDTO request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getLimit());

        Specification<CustomerOrder> spec = Specification.where(CustomerOrderSpecification.hasUserId(request.getUserId()))
                .and(CustomerOrderSpecification.hasStatusId(request.getCustomerOrderStatusId()))
                .and(CustomerOrderSpecification.hasSearchQuery(request.getSearchQuery()))
                .and(CustomerOrderSpecification.hasStartDate(request.getStartDate()))
                .and(CustomerOrderSpecification.hasEndDate(request.getEndDate()));

        Page<CustomerOrder> customerOrderPage = customerOrderRepository.findAll(spec, pageable);

        List<CustomerOrderResponseDTO> orderResponses = customerOrderPage.getContent()
                .stream()
                .map(CustomerOrderResponseDTO::mapToDTO)
                .toList();

        return new PaginationInfo<>(customerOrderPage, orderResponses);
    }

    @Override
    public CustomerOrderResponseDTO getCustomerOrder(CustomerOrderDetailRequestDTO request) {
        // Verify the user is exist
        User user = usersRepository.findById(request.getUserId())
                .orElseThrow(() -> new DataNotFoundException("User not found."));

        // Check is user verified as customer
        if(!"CUSTOMER_VERIFIED".equals(user.getRole().getName())) {
            throw new DataNotFoundException("Customer is not verified.");
        }

        // Check if the order exist
        CustomerOrder customerOrder = customerOrderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new DataNotFoundException("Order not found."));

        return CustomerOrderResponseDTO.mapToDTO(customerOrder);
    }

    @Override
    public CustomerOrderResponseDTO confirmCustomerOrder(ConfirmOrderRequestDTO request) {
        // Verify the user is exist
        User user = usersRepository.findById(request.getUserId())
                .orElseThrow(() -> new DataNotFoundException("User not found."));

        // Check is user verified as customer
        if(!"CUSTOMER_VERIFIED".equals(user.getRole().getName())) {
            throw new DataNotFoundException("Customer is not verified.");
        }

        // Check if the order exist
        CustomerOrder customerOrder = customerOrderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new DataNotFoundException("Order not found."));

        // Fetch the "Shipped" status
        CustomerOrderStatus shippedStatus = customerOrderStatusRepository.findByName("Shipped")
                .orElseThrow(() -> new DataNotFoundException("Order status 'Shipped' not found."));

        // Check if the current order status is "Shipped"
        if (!customerOrder.getOrderStatus().equals(shippedStatus)) {
            throw new IllegalArgumentException("Cannot confirm the order, status is not 'Shipped'.");
        }

        // Fetch the "Confirmed" status (ID 5)
        CustomerOrderStatus confirmedStatus = customerOrderStatusRepository.findByName("Confirmed")
                .orElseThrow(() -> new DataNotFoundException("Order status 'Confirmed' not found."));

        // Update order status to "Confirmed"
        customerOrder.setOrderStatus(confirmedStatus);
        customerOrderRepository.save(customerOrder);

        return CustomerOrderResponseDTO.mapToDTO(customerOrder);
    }
}
