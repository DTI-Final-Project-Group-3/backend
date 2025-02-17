package com.warehub.warehub.usecase.transaction.impl;

import com.warehub.warehub.common.exceptions.DataNotFoundException;
import com.warehub.warehub.entity.*;
import com.warehub.warehub.infrastructure.customerOrderItems.repository.CustomerOrderItemsRepository;
import com.warehub.warehub.infrastructure.customerOrderStatus.CustomerOrderStatusRepository;
import com.warehub.warehub.infrastructure.customerOrders.repository.CustomerOrderRepository;
import com.warehub.warehub.infrastructure.paymentMethod.repository.PaymentMethodRepository;
import com.warehub.warehub.infrastructure.product.repository.ProductRepository;
import com.warehub.warehub.infrastructure.transaction.dto.ManualTransactionRequestDTO;
import com.warehub.warehub.infrastructure.transaction.dto.ManualTransactionResponseDTO;
import com.warehub.warehub.infrastructure.users.repository.UsersRepository;
import com.warehub.warehub.infrastructure.warehouse.repository.WarehouseRepository;
import com.warehub.warehub.usecase.transaction.ManualTransactionUsecase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ManualTransactionUsecaseImpl implements ManualTransactionUsecase {
    private final CustomerOrderRepository customerOrderRepository;
    private final CustomerOrderItemsRepository customerOrderItemsRepository;
    private final UsersRepository usersRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final CustomerOrderStatusRepository orderStatusRepository;

    public ManualTransactionUsecaseImpl(
            CustomerOrderRepository customerOrderRepository,
            CustomerOrderItemsRepository customerOrderItemsRepository,
            UsersRepository usersRepository,
            WarehouseRepository warehouseRepository,
            ProductRepository productRepository,
            PaymentMethodRepository paymentMethodRepository,
            CustomerOrderStatusRepository orderStatusRepository)
    {
        this.customerOrderRepository = customerOrderRepository;
        this.customerOrderItemsRepository = customerOrderItemsRepository;
        this.usersRepository = usersRepository;
        this.warehouseRepository = warehouseRepository;
        this.productRepository = productRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.orderStatusRepository = orderStatusRepository;
    }

    @Transactional
    @Override
    public ManualTransactionResponseDTO createManualTransaction(ManualTransactionRequestDTO request) {
        // Validate user
        User user = usersRepository.findById(request.getUserId())
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        // Validate warehouse
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new DataNotFoundException("Warehouse not found"));

        // Validate payment method
        PaymentMethod paymentMethod = paymentMethodRepository.findById(request.getPaymentMethodId())
                .orElseThrow(() -> new DataNotFoundException("PaymentMethod not found"));

        // Validate order status
        CustomerOrderStatus orderStatus = orderStatusRepository.findById(request.getOrderStatusId())
                .orElseThrow(() -> new DataNotFoundException("OrderStatus not found"));

        // Generate invoice code
        String invoiceCode = "ORDER-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);

        // Create and save customer order
        CustomerOrder customerOrder = new CustomerOrder();
        customerOrder.setUser(user);
        customerOrder.setWarehouse(warehouse);
        customerOrder.setPaymentMethod(paymentMethod);
        customerOrder.setShippingCost(request.getShippingCost());
        customerOrder.setTotalAmount(request.getGrossAmount());
        customerOrder.setOrderStatus(orderStatus);
        customerOrder.setInvoiceCode(invoiceCode);
        customerOrder.setPaymentProofImageUrl(request.getPaymentProofUrl());

        customerOrder = customerOrderRepository.save(customerOrder);

        // Save order items
        CustomerOrder finalCustomerOrder = customerOrder;

        List<CustomerOrderItem> orderItems = request.getOrderItems().stream().map(item -> {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new DataNotFoundException("Product not found"));

            CustomerOrderItem orderItem = new CustomerOrderItem();
            orderItem.setCustomerOrder(finalCustomerOrder);
            orderItem.setProduct(product);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setProductPrice(item.getUnitPrice());

            return orderItem;
        }).toList();

        customerOrderItemsRepository.saveAll(orderItems);

        // Construct return response
        return new ManualTransactionResponseDTO(
                customerOrder.getId(),
                invoiceCode,
                customerOrder.getTotalAmount(),
                customerOrder.getShippingCost(),
                customerOrder.getPaymentProofImageUrl(),
                user.getId(),
                warehouse.getId(),
                paymentMethod.getId(),
                Math.toIntExact(orderStatus.getId()),
                request.getOrderItems(),
                customerOrder.getCreatedAt(),
                customerOrder.getUpdatedAt(),
                orderStatus.getName(),
                customerOrder.getGatewayTrxId()
        );
    }
}
