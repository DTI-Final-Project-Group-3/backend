package com.warehub.warehub.usecase.customerOrder.impl;

import com.warehub.warehub.common.exceptions.DataNotFoundException;
import com.warehub.warehub.entity.CustomerOrder;
import com.warehub.warehub.entity.CustomerOrderStatus;
import com.warehub.warehub.entity.User;
import com.warehub.warehub.entity.enums.OrderStatuses;
import com.warehub.warehub.entity.enums.PaymentMethods;
import com.warehub.warehub.infrastructure.customerOrderStatus.CustomerOrderStatusRepository;
import com.warehub.warehub.infrastructure.customerOrders.dto.CustomerOrderResponseDTO;
import com.warehub.warehub.infrastructure.customerOrders.dto.UpdateOrderRequestDTO;
import com.warehub.warehub.infrastructure.customerOrders.repository.CustomerOrderRepository;
import com.warehub.warehub.infrastructure.users.repository.UsersRepository;
import com.warehub.warehub.usecase.customerOrder.AdminCustomerOrderUsecase;
import com.warehub.warehub.usecase.customerOrder.CustomerOrderUsecase;
import com.warehub.warehub.usecase.transaction.ManualTransactionUsecase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminCustomerOrderUsecaseImpl implements AdminCustomerOrderUsecase {

    private final UsersRepository usersRepository;
    private final CustomerOrderRepository customerOrderRepository;
    private final CustomerOrderStatusRepository customerOrderStatusRepository;
    private final ManualTransactionUsecase manualTransactionUsecase;
    private final CustomerOrderUsecase customerOrderUsecase;

    @Override
    public CustomerOrderResponseDTO updateCustomerOrder(UpdateOrderRequestDTO requestDTO) {
        User user = usersRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("User Not Found"));

        System.out.println("Request DTO " +requestDTO);

        // 3L = ADMIN_WAREHOUSE | 4L = ADMIN_SUPER
        Set<Long> allowedRoles = Set.of(3L, 4L);
        if (!allowedRoles.contains(user.getRole().getId())) throw new IllegalArgumentException("User is not a SUPER_ADMIN nor WAREHOUSE_ADMIN");

        CustomerOrder customerOrder = customerOrderRepository.findById(requestDTO.getOrderId())
                .orElseThrow(() -> new DataNotFoundException("Order with ID " + requestDTO.getOrderId() + " not found"));

        // Check if the order using manual payment method
        if (!customerOrder.getPaymentMethod().getId().equals(PaymentMethods.PAYMENT_MANUAL_METHOD.getId())) {
            throw new IllegalArgumentException("This order does not use manual payment and cannot be updated this way.");
        }

        // Retrieve necessary statuses from repository
        CustomerOrderStatus canceledStatus = getOrderStatus(OrderStatuses.CANCELED);
        CustomerOrderStatus waitingPaymentStatus = getOrderStatus(OrderStatuses.WAITING_PAYMENT);
        CustomerOrderStatus waitingForAdminConfirmationStatus = getOrderStatus(OrderStatuses.WAITING_PAYMENT_CONFIRMATION);
        CustomerOrderStatus processedStatus = getOrderStatus(OrderStatuses.PROCESSED);

        // Prevent redundant updates if the order already canceled
        if (customerOrder.getOrderStatus().getId().equals(canceledStatus.getId())) throw new RuntimeException("Order with ID " +requestDTO.getOrderId()+ " is already canceled");

        /*
        * Approve or reject customer payment proof image for the order
        * */
        OffsetDateTime createdAt = customerOrder.getCreatedAt();
        OffsetDateTime now = OffsetDateTime.now();
        long hoursSinceCreation = Duration.between(createdAt, now).toHours();

        boolean hasPaymentProof = customerOrder.getPaymentProofImageUrl() != null && !customerOrder.getPaymentProofImageUrl().isEmpty();

        if (customerOrder.getOrderStatus().getId().equals(waitingPaymentStatus.getId())) {
            // If payment proof is missing and more than 1 hour has passed → CANCEL ORDER
            if (!hasPaymentProof && hoursSinceCreation >= 1) {
                customerOrder.setOrderStatus(canceledStatus);
            }
            // If payment proof is available → MOVE TO WAITING_PAYMENT_CONFIRMATION
            else if (hasPaymentProof) {
                customerOrder.setOrderStatus(waitingForAdminConfirmationStatus);
            }
        }

        // If order is in WAITING_PAYMENT_CONFIRMATION
        if (customerOrder.getOrderStatus().getId().equals(waitingForAdminConfirmationStatus.getId())) {
            if (Boolean.TRUE.equals(requestDTO.getIsAdminApproved())) {
                // If admin approves → MOVE TO PROCESSED
                customerOrder.setOrderStatus(processedStatus);
            } else {
                // If admin rejects → MOVE BACK TO WAITING_PAYMENT and clear payment proof
                customerOrder.setOrderStatus(waitingPaymentStatus);
                customerOrder.setPaymentProofImageUrl(null);
            }
        }

        // Always save after updating the status
        customerOrderRepository.save(customerOrder);
        return CustomerOrderResponseDTO.mapToDTO(customerOrder);
    }

    @Override
    public CustomerOrderResponseDTO processCustomerOrder(UpdateOrderRequestDTO requestDTO) {
        User user = usersRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("User Not Found"));


        // 3L = ADMIN_WAREHOUSE | 4L = ADMIN_SUPER
        Set<Long> allowedRoles = Set.of(3L, 4L);
        if (!allowedRoles.contains(user.getRole().getId())) throw new IllegalArgumentException("User is not a SUPER_ADMIN nor WAREHOUSE_ADMIN");

        CustomerOrder customerOrder = customerOrderRepository.findById(requestDTO.getOrderId())
                .orElseThrow(() -> new DataNotFoundException("Order with ID " + requestDTO.getOrderId() + " not found"));

        // Check if the order using manual payment method
        if (!customerOrder.getPaymentMethod().getId().equals(PaymentMethods.PAYMENT_MANUAL_METHOD.getId())) {
            throw new IllegalArgumentException("This order does not use manual payment and cannot be updated this way.");
        }

        // Retrieve necessary statuses from repository
        CustomerOrderStatus waitingForAdminConfirmationStatus = getOrderStatus(OrderStatuses.WAITING_PAYMENT_CONFIRMATION);
        CustomerOrderStatus processedStatus = getOrderStatus(OrderStatuses.PROCESSED);
        CustomerOrderStatus shippedPaymentStatus = getOrderStatus(OrderStatuses.SHIPPED);
        CustomerOrderStatus canceledStatus = getOrderStatus(OrderStatuses.CANCELED);

        // Prevent redundant updates if the order already canceled
        if (customerOrder.getOrderStatus().getId().equals(canceledStatus.getId())) throw new RuntimeException("Order with ID " +requestDTO.getOrderId()+ " is already canceled");
        
        // If order is in WAITING_PAYMENT_CONFIRMATION and admin approves, move to PROCESSED
        if (customerOrder.getOrderStatus().getId().equals(waitingForAdminConfirmationStatus.getId())
                && Boolean.TRUE.equals(requestDTO.getIsAdminApproved())) {
            customerOrder.setOrderStatus(processedStatus);
            customerOrderRepository.save(customerOrder);
        }

        // If order is already PROCESSED, move to SHIPPED
        if (customerOrder.getOrderStatus().getId().equals(processedStatus.getId())
                && Boolean.TRUE.equals(requestDTO.getIsAdminApproved())) {
            customerOrder.setOrderStatus(shippedPaymentStatus);
            customerOrder.setSentAt(OffsetDateTime.now());
            customerOrderRepository.save(customerOrder);
        }

        return CustomerOrderResponseDTO.mapToDTO(customerOrder);
    }

    @Override
    public void autoUpdateCustomerOrderStatus() {
        // Get all lists of waiting for payment orders
        List<CustomerOrder> pendingOrders = customerOrderRepository.findByOrderStatusId(1L);
        if (pendingOrders.isEmpty()) return;

        CustomerOrderStatus waitingForAdminConfirmation = getOrderStatus(OrderStatuses.WAITING_PAYMENT_CONFIRMATION);
        OffsetDateTime now = OffsetDateTime.now();

        for (CustomerOrder order : pendingOrders) {
            OffsetDateTime createdAt = order.getCreatedAt();
            long hoursSinceCreation = Duration.between(createdAt, now).toHours();
            boolean hasPaymentProof = order.getPaymentProofImageUrl() != null && !order.getPaymentProofImageUrl().isEmpty();
            boolean isPaymentGateway = order.getPaymentMethod().getId().equals((long) OrderStatuses.WAITING_PAYMENT.getId());

            if (isPaymentGateway) {
                if (hoursSinceCreation >= 1) {
                    customerOrderUsecase.cancelCustomerOrder(order.getId());
                }
            } else {
                if(!hasPaymentProof && hoursSinceCreation >= 1) {
                    customerOrderUsecase.cancelCustomerOrder(order.getId());
                } else if (hasPaymentProof && hoursSinceCreation < 1) {
                    order.setOrderStatus(waitingForAdminConfirmation);
                    customerOrderRepository.save(order);
                }
            }
        }
    }

    @Override
    public void autoConfirmCustomerOrderStatus() {
        OffsetDateTime twoDaysAgo = OffsetDateTime.now().minusDays(2);

        CustomerOrderStatus shippedStatus = getOrderStatus(OrderStatuses.SHIPPED);
        CustomerOrderStatus confirmedStatus = getOrderStatus(OrderStatuses.CONFIRMED);

        // Get all customer order with SHIPPED status and were sent more than 2 days ago
        List<CustomerOrder> ordersToConfirm = customerOrderRepository.findByOrderStatusIdAndSentAtBefore(shippedStatus.getId(), twoDaysAgo);

        if (!ordersToConfirm.isEmpty()) {
            ordersToConfirm.forEach(order -> order.setOrderStatus(confirmedStatus));
            customerOrderRepository.saveAll(ordersToConfirm);
            System.out.println("Shipped orders were auto confirmed " +ordersToConfirm.size());
        } else {
            System.out.println("No shipped orders needed confirmation.");
        }
    }

    private CustomerOrderStatus getOrderStatus(OrderStatuses status) {
        return customerOrderStatusRepository.findById(status.getId())
                .orElseThrow(() -> new DataNotFoundException("Order status " + status.getStatus() + " not found"));
    }
}

