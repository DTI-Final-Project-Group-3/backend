package com.warehub.warehub.usecase.customerOrder.impl;

import com.warehub.warehub.common.exceptions.DataNotFoundException;
import com.warehub.warehub.entity.CustomerOrder;
import com.warehub.warehub.entity.CustomerOrderStatus;
import com.warehub.warehub.entity.User;
import com.warehub.warehub.infrastructure.customerOrderStatus.CustomerOrderStatusRepository;
import com.warehub.warehub.infrastructure.customerOrders.dto.CustomerOrderResponseDTO;
import com.warehub.warehub.infrastructure.customerOrders.dto.UpdateOrderRequestDTO;
import com.warehub.warehub.infrastructure.customerOrders.repository.CustomerOrderRepository;
import com.warehub.warehub.infrastructure.users.repository.UsersRepository;
import com.warehub.warehub.usecase.customerOrder.AdminCustomerOrderUsecase;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

@Service
public class AdminCustomerOrderUsecaseImpl implements AdminCustomerOrderUsecase {

    private final UsersRepository usersRepository;
    private final CustomerOrderRepository customerOrderRepository;
    private final CustomerOrderStatusRepository customerOrderStatusRepository;

    public AdminCustomerOrderUsecaseImpl(UsersRepository usersRepository, CustomerOrderRepository customerOrderRepository, CustomerOrderStatusRepository customerOrderStatusRepository) {
        this.usersRepository = usersRepository;
        this.customerOrderRepository = customerOrderRepository;
        this.customerOrderStatusRepository = customerOrderStatusRepository;
    }

    @Override
    public CustomerOrderResponseDTO updateCustomerOrder(UpdateOrderRequestDTO requestDTO) {
        User user = usersRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("User Not Found"));

        /*
        * 3L = ADMIN_WAREHOUSE
        * 4L = ADMIN_SUPER
        * */
        Set<Long> allowedRoles = Set.of(3L, 4L);
        if (!allowedRoles.contains(user.getRole().getId())) {
            throw new IllegalArgumentException("User is not an admin");
        }

        CustomerOrder customerOrder = customerOrderRepository.findById(requestDTO.getOrderId())
                .orElseThrow(() -> new DataNotFoundException("Order with ID " + requestDTO.getOrderId() + " not found"));

        // Prevent redundant updates if the order already canceled
        CustomerOrderStatus cancelPayment = customerOrderStatusRepository.findById(6)
                .orElseThrow(() -> new DataNotFoundException("Customer order status cancel payment not found"));
        if (customerOrder.getOrderStatus().getId().equals(cancelPayment.getId())) {
            throw new RuntimeException("Order with ID " +requestDTO.getOrderId()+ " is already canceled");
        }

        CustomerOrderStatus waitingPayment = customerOrderStatusRepository.findById(1)
                .orElseThrow(() -> new DataNotFoundException("Customer order status waiting for payment not found"));

        CustomerOrderStatus waitingForAdminConfirmation = customerOrderStatusRepository.findById(2)
                .orElseThrow(() -> new DataNotFoundException("Customer order status waiting for admin confirmation not found"));

        /*
        * Cancel order when the createdAt already more than 1 hour without uploading payment proof image
        * */
        OffsetDateTime createdAt = customerOrder.getCreatedAt();
        OffsetDateTime now = OffsetDateTime.now();
        long hoursSinceCreation = Duration.between(createdAt, now).toHours();

        boolean hasPaymentProof = customerOrder.getPaymentProofImageUrl() != null && !customerOrder.getPaymentProofImageUrl().isEmpty();
        boolean isPaymentGateway = customerOrder.getPaymentMethod().getId() == 1;

        if (customerOrder.getOrderStatus().getId().equals(waitingPayment.getId())) {
            if (isPaymentGateway) {
                // Cancel order with payment gateway method if unpaid after 1 hour
                if (hoursSinceCreation >= 1) {
                   customerOrder.setOrderStatus(cancelPayment);
                   customerOrderRepository.save(customerOrder);
                }
            } else {
                // Automatically cancel the order if already passed 1hr from order createdAt
                if (!hasPaymentProof && hoursSinceCreation >= 1) {
                    customerOrder.setOrderStatus(cancelPayment);
                }
                // Approve if payment proof image is uploaded within 1 hour
                else if (hasPaymentProof && hoursSinceCreation < 1) {
                    customerOrder.setOrderStatus(waitingForAdminConfirmation);
                }
                customerOrderRepository.save(customerOrder);
            }
        }
        return CustomerOrderResponseDTO.mapToDTO(customerOrder);
    }

    @Override
    public void autoUpdateCustomerOrderStatus() {
        // Get all lists of pending orders
        List<CustomerOrder> pendingOrders = customerOrderRepository.findByOrderStatusId(1L);

        if (pendingOrders.isEmpty()) return;

        CustomerOrderStatus cancelPayment = customerOrderStatusRepository.findById(6)
                .orElseThrow(() -> new DataNotFoundException("Cancel payment status not found"));

        CustomerOrderStatus waitingForAdminConfirmation = customerOrderStatusRepository.findById(2)
                .orElseThrow(() -> new DataNotFoundException("Waiting for admin confirmation status not found"));

        OffsetDateTime now = OffsetDateTime.now();

        for (CustomerOrder order : pendingOrders) {
            OffsetDateTime createdAt = order.getCreatedAt();
            long hoursSinceCreation = Duration.between(createdAt, now).toHours();
            boolean hasPaymentProof = order.getPaymentProofImageUrl() != null && !order.getPaymentProofImageUrl().isEmpty();
            boolean isPaymentGateway = order.getPaymentMethod().getId() == 1;

            if (isPaymentGateway) {
                if (hoursSinceCreation >= 1) {
                    order.setOrderStatus(cancelPayment);
                }
            } else {
                if(!hasPaymentProof && hoursSinceCreation >= 1) {
                    order.setOrderStatus(cancelPayment);
                } else if (hasPaymentProof && hoursSinceCreation < 1) {
                    order.setOrderStatus(waitingForAdminConfirmation);
                }
            }
        }
        customerOrderRepository.saveAll(pendingOrders);
    }
}

