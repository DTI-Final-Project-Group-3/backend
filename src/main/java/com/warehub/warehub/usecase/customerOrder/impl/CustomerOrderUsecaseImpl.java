package com.warehub.warehub.usecase.customerOrder.impl;

import com.warehub.warehub.common.enums.MutationConstant;
import com.warehub.warehub.common.exceptions.DataNotFoundException;
import com.warehub.warehub.common.utils.CreateProductMutationLog;
import com.warehub.warehub.common.utils.PaginationInfo;
import com.warehub.warehub.entity.*;
import com.warehub.warehub.entity.enums.OrderStatuses;
import com.warehub.warehub.entity.enums.RoleType;
import com.warehub.warehub.infrastructure.customerOrderItems.repository.CustomerOrderItemsRepository;
import com.warehub.warehub.infrastructure.customerOrderStatus.CustomerOrderStatusRepository;
import com.warehub.warehub.infrastructure.customerOrders.dto.*;
import com.warehub.warehub.infrastructure.customerOrders.repository.CustomerOrderRepository;
import com.warehub.warehub.infrastructure.customerOrders.specification.CustomerOrderSpecification;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationRepository;
import com.warehub.warehub.infrastructure.users.repository.UsersRepository;
import com.warehub.warehub.infrastructure.warehouse.repository.WarehouseAdminRepository;
import com.warehub.warehub.infrastructure.warehouseInventory.repository.WarehouseInventoryRepository;
import com.warehub.warehub.usecase.customerOrder.CustomerOrderUsecase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerOrderUsecaseImpl implements CustomerOrderUsecase {
    private final CustomerOrderRepository customerOrderRepository;
    private final UsersRepository usersRepository;
    private final CustomerOrderStatusRepository customerOrderStatusRepository;
    private final WarehouseAdminRepository warehouseAdminRepository;
    private final CustomerOrderItemsRepository customerOrderItemsRepository;
    private final ProductMutationRepository productMutationRepository;
    private final WarehouseInventoryRepository warehouseInventoryRepository;
    private final CreateProductMutationLog createProductMutationLog;

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
                .and(CustomerOrderSpecification.hasWarehouseId(request.getWarehouseId()))
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
        CustomerOrderStatus shippedStatus = getOrderStatus(OrderStatuses.SHIPPED);

        // Check if the current order status is "Shipped"
        if (!customerOrder.getOrderStatus().equals(shippedStatus)) {
            throw new IllegalArgumentException("Cannot confirm the order, status is not 'Shipped'.");
        }

        // Fetch the "Confirmed" status
        CustomerOrderStatus confirmedStatus = getOrderStatus(OrderStatuses.CONFIRMED);

        // Update order status to "Confirmed"
        customerOrder.setOrderStatus(confirmedStatus);
        customerOrderRepository.save(customerOrder);

        return CustomerOrderResponseDTO.mapToDTO(customerOrder);
    }

    @Override
    public CustomerOrderResponseDTO cancelCustomerOrder(Long customerOrderId) {
        // Find the user
//        User user = usersRepository.findById(userId)
//                .orElseThrow(() -> new DataNotFoundException("User not found"));

        // Find the customer order
        CustomerOrder customerOrder = customerOrderRepository.findById(customerOrderId)
                .orElseThrow(() -> new DataNotFoundException("Customer order not found"));

        // Get order items
        List<CustomerOrderItem> orderItems = customerOrderItemsRepository.findByCustomerOrderId(customerOrderId);

        if (orderItems.isEmpty()) throw new DataNotFoundException("No oder items found for this transaction");

        // Reverse stock changes
        for (CustomerOrderItem item : orderItems) {
            Product product = item.getProduct();
            int quantityToReturn = item.getQuantity();

            // Find product mutation records (to track stock movement)
            List<ProductMutation> mutations = productMutationRepository.findByInvoiceCodeAndProductId(
                    customerOrder.getInvoiceCode(), product.getId());

            for (ProductMutation mutation : mutations) {
                Warehouse originWarehouse = mutation.getOriginWarehouse();
                Warehouse destinationWarehouse = mutation.getDestinationWarehouse();
                int mutatedQuantity = mutation.getQuantity(); // The actual quantity moved in mutation

                // Stock was moved out (deducted)
                if (mutatedQuantity < 0) {
                    // Restore stock in the origin warehouse
                    WarehouseInventory originInventory = warehouseInventoryRepository
                            .findByProductIdAndWarehouseIdAndDeletedAtIsNull(product.getId(), originWarehouse.getId())
                            .orElseThrow(() -> new DataNotFoundException("Inventory record not found"));

                    // Restore only what is needed
                    int quantityToRestore = Math.min(quantityToReturn, Math.abs(mutatedQuantity));
                    originInventory.setQuantity(originInventory.getQuantity() + quantityToRestore);
                    warehouseInventoryRepository.save(originInventory);


                    // Log mutation (reverse stock movement)
                    createProductMutationLog.createProductMutationRecord(
                            product, quantityToRestore, "Order canceled : reversing transaction cancellation", customerOrder.getUser(),
                            originWarehouse, destinationWarehouse,
                            MutationConstant.TYPE_INBOUND_AUTO_MUTATION.getValue(), MutationConstant.STATUS_CANCELLED.getValue(),
                            customerOrder.getInvoiceCode()
                    );

                    // Log mutation (reverse stock movement)
                    createProductMutationLog.createProductMutationRecord(
                            product, -quantityToRestore, "Order canceled : reversing transaction cancellation", customerOrder.getUser(),
                            originWarehouse, destinationWarehouse,
                            MutationConstant.TYPE_OUTBOUND_AUTO_MUTATION.getValue(), MutationConstant.STATUS_CANCELLED.getValue(),
                            customerOrder.getInvoiceCode()
                    );

                    // Reduce remaining quantity to return
                    quantityToReturn -= quantityToRestore;
                    if (quantityToReturn <= 0) break; // Stop if all stock is restored
                }
            }
        }
        // Mark order as canceled
        CustomerOrderStatus canceledStatus = getOrderStatus(OrderStatuses.CANCELED);
        customerOrder.setOrderStatus(canceledStatus);
        customerOrderRepository.save(customerOrder);

        return CustomerOrderResponseDTO.mapToDTO(customerOrder);
    }

    @Override
    public PaginationInfo<CustomerOrderHistoryResponseDTO> getHistoryCustomerOrder(CustomerOrderHistoryRequestDTO req) {
        PageRequest pageRequest = PageRequest.of(req.getPage(), req.getLimit());

        Page<CustomerOrderHistoryResponseDTO> responseDTO = customerOrderRepository
                .findHistoryCustomerOrderByFilter(req.getStartDate(), req.getEndDate(),
                        req.getWarehouseId(),
                        req.getCustomerOrderStatusId(),
                        req.getProductId(), req.getProductCategoryId(),
                        pageRequest);

        return new PaginationInfo<>(responseDTO, responseDTO.getContent());
    }

    @Override
    public List<CustomerOrderDailyTotalResponseDTO> getDailyTotalCustomerOrder(CustomerOrderHistoryRequestDTO req) {

        ZoneOffset utc7Offset = ZoneOffset.ofHours(7);
        OffsetDateTime startDate = req.getStartDate().atStartOfDay().atOffset(utc7Offset);
        OffsetDateTime endDate = req.getEndDate().plusDays(1).atStartOfDay().atOffset(utc7Offset);

        return customerOrderRepository
                .findDailyTotalByFilter(startDate, endDate,
                        req.getWarehouseId(),
                        req.getCustomerOrderStatusId(),
                        req.getProductId(), req.getProductCategoryId());
    }

    private CustomerOrderStatus getOrderStatus(OrderStatuses status) {
        return customerOrderStatusRepository.findById(status.getId())
                .orElseThrow(() -> new DataNotFoundException("Order status " + status.getStatus() + " not found"));
    }
}
