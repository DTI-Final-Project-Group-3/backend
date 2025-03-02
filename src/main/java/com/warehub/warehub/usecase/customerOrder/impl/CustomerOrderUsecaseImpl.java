package com.warehub.warehub.usecase.customerOrder.impl;

import com.warehub.warehub.common.exceptions.DataNotFoundException;
import com.warehub.warehub.common.utils.PaginationInfo;
import com.warehub.warehub.entity.CustomerOrder;
import com.warehub.warehub.entity.CustomerOrderStatus;
import com.warehub.warehub.entity.User;
import com.warehub.warehub.entity.WarehouseAdmin;
import com.warehub.warehub.entity.enums.OrderStatuses;
import com.warehub.warehub.entity.enums.RoleType;
import com.warehub.warehub.infrastructure.customerOrderStatus.CustomerOrderStatusRepository;
import com.warehub.warehub.infrastructure.customerOrders.dto.ConfirmOrderRequestDTO;
import com.warehub.warehub.infrastructure.customerOrders.dto.CustomerOrderResponseDTO;
import com.warehub.warehub.infrastructure.customerOrders.dto.CustomerOrderDetailRequestDTO;
import com.warehub.warehub.infrastructure.customerOrders.dto.PaginatedCustomerOrderRequestDTO;
import com.warehub.warehub.infrastructure.customerOrders.repository.CustomerOrderRepository;
import com.warehub.warehub.infrastructure.customerOrders.specification.CustomerOrderSpecification;
import com.warehub.warehub.infrastructure.users.repository.UsersRepository;
import com.warehub.warehub.infrastructure.warehouse.repository.WarehouseAdminRepository;
import com.warehub.warehub.usecase.customerOrder.CustomerOrderUsecase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerOrderUsecaseImpl implements CustomerOrderUsecase {
    private final CustomerOrderRepository customerOrderRepository;
    private final UsersRepository usersRepository;
    private final CustomerOrderStatusRepository customerOrderStatusRepository;
    private final WarehouseAdminRepository warehouseAdminRepository;

    public CustomerOrderUsecaseImpl(CustomerOrderRepository customerOrderRepository,
                                    UsersRepository usersRepository,
                                    CustomerOrderStatusRepository customerOrderStatusRepository,
                                    WarehouseAdminRepository warehouseAdminRepository) {
        this.customerOrderRepository = customerOrderRepository;
        this.usersRepository = usersRepository;
        this.customerOrderStatusRepository = customerOrderStatusRepository;
        this.warehouseAdminRepository = warehouseAdminRepository;
    }

    @Override
    public PaginationInfo<CustomerOrderResponseDTO> getAllCustomerOrders(PaginatedCustomerOrderRequestDTO request) {
        // Fetch user data
        User user = usersRepository.findById(request.getUserId())
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        Pageable pageable = PageRequest.of(request.getPage(), request.getLimit(), Sort.by(Sort.Direction.DESC, "createdAt"));

        // Start with base specification
        Specification<CustomerOrder> spec = Specification.where(null);

        // Filter data based on user ROLE
        if (user.getRole().getName().equals(RoleType.CUSTOMER_VERIFIED.name())) {
            // Show only orders belong to this user
            spec = spec.and(CustomerOrderSpecification.hasUserId(user.getId()));
        } else if (user.getRole().getName().equals(RoleType.ADMIN_SUPER.name())) {
            // If `warehouseId` is provided, filter orders for that warehouse
            if (request.getWarehouseId() != null) {
                spec = spec.and(CustomerOrderSpecification.hasWarehouseId(request.getWarehouseId()));
            } else {
                // No filter for this role, just Explicitly show all orders
                spec = spec.and(Specification.where(null));
            }
        } else if (user.getRole().getName().equals(RoleType.ADMIN_WAREHOUSE.name())) {
            // SHow only orders that belong to the warehouse where this admin is assigned
            Optional<WarehouseAdmin> warehouseAdmin = warehouseAdminRepository.findByUserAssigneeId(user.getId());

            // Ensure warehouseAdmin is present before accessing its warehouse
            if (warehouseAdmin.isEmpty()) {
                throw new DataNotFoundException("Warehouse admin not found for this user");
            }

            // Restrict access to only their assigned warehouse
            spec = spec.and(CustomerOrderSpecification.hasWarehouseId(warehouseAdmin.get().getWarehouse().getId()));

            /// If a warehouseId is provided and it's NOT their assigned warehouse, throw an error
            if (request.getWarehouseId() != null && !request.getWarehouseId().equals(warehouseAdmin.get().getWarehouse().getId())) {
                throw new IllegalArgumentException("You're not assigned to this warehouse");
            }
        } else {
            throw new IllegalArgumentException("Unauthorized role access");
        }

        // Apply additional filters from request params
        spec = spec.and(CustomerOrderSpecification.hasStatusId(request.getCustomerOrderStatusId()))
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

        // Define allowed roles
        List<String> allowedRoles = Arrays.asList("CUSTOMER_VERIFIED", "ADMIN_WAREHOUSE", "ADMIN_SUPER");

        // Check if the user has one of the allowed roles
        if (!allowedRoles.contains(user.getRole().getName())) {
            throw new IllegalArgumentException("You do not have permission to access this order.");
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
        CustomerOrderStatus shippedStatus = customerOrderStatusRepository.findByName(OrderStatuses.SHIPPED.getStatus())
                .orElseThrow(() -> new DataNotFoundException("Order status 'Shipped' not found."));

        // Check if the current order status is "Shipped"
        if (!customerOrder.getOrderStatus().equals(shippedStatus)) {
            throw new IllegalArgumentException("Cannot confirm the order, status is not 'Shipped'.");
        }

        // Fetch the "Confirmed" status
        CustomerOrderStatus confirmedStatus = customerOrderStatusRepository.findByName(OrderStatuses.CONFIRMED.getStatus())
                .orElseThrow(() -> new DataNotFoundException("Order status 'Confirmed' not found."));

        // Update order status to "Confirmed"
        customerOrder.setOrderStatus(confirmedStatus);
        customerOrderRepository.save(customerOrder);

        return CustomerOrderResponseDTO.mapToDTO(customerOrder);
    }
}
