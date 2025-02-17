package com.warehub.warehub.usecase.transaction.impl;

import com.midtrans.Midtrans;
import com.midtrans.httpclient.SnapApi;
import com.midtrans.httpclient.error.MidtransError;
import com.warehub.warehub.common.exceptions.DataNotFoundException;
import com.warehub.warehub.entity.*;
import com.warehub.warehub.infrastructure.customerOrderItems.repository.CustomerOrderItemsRepository;
import com.warehub.warehub.infrastructure.customerOrderStatus.CustomerOrderStatusRepository;
import com.warehub.warehub.infrastructure.customerOrders.repository.CustomerOrderRepository;
import com.warehub.warehub.infrastructure.paymentMethod.repository.PaymentMethodRepository;
import com.warehub.warehub.infrastructure.product.repository.ProductRepository;
import com.warehub.warehub.infrastructure.transaction.dto.TransactionRequestDTO;
import com.warehub.warehub.infrastructure.transaction.dto.TransactionResponseDTO;
import com.warehub.warehub.infrastructure.users.repository.UsersRepository;
import com.warehub.warehub.infrastructure.warehouse.repository.WarehouseRepository;
import com.warehub.warehub.usecase.transaction.TransactionUsecase;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TransactionUsecaseImpl implements TransactionUsecase {
    private final CustomerOrderRepository customerOrderRepository;
    private final CustomerOrderItemsRepository customerOrderItemsRepository;
    private final UsersRepository usersRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final CustomerOrderStatusRepository customerOrderStatusRepository;

    public TransactionUsecaseImpl(
            CustomerOrderRepository customerOrderRepository,
            CustomerOrderItemsRepository customerOrderItemsRepository,
            UsersRepository usersRepository,
            WarehouseRepository warehouseRepository,
            ProductRepository productRepository,
            PaymentMethodRepository paymentMethodRepository,
            CustomerOrderStatusRepository customerOrderStatusRepository)
    {
        this.customerOrderRepository = customerOrderRepository;
        this.customerOrderItemsRepository = customerOrderItemsRepository;
        this.usersRepository = usersRepository;
        this.warehouseRepository = warehouseRepository;
        this.productRepository = productRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.customerOrderStatusRepository = customerOrderStatusRepository;
    }

    // Set serverKey to Midtrans global config
    static {
        Midtrans.serverKey =  System.getenv("MIDTRANS_SERVER_KEY");
        Midtrans.isProduction = false;
    }

    @Override
    public TransactionResponseDTO createTransaction(TransactionRequestDTO trxRequest) {
        // Create params JSON Raw Object request
        Map<String, Object> params = new HashMap<>();
        String randomOrderId = UUID.randomUUID().toString();

        Map<String, Object> transactionDetails = new HashMap<>();
        transactionDetails.put("order_id", trxRequest.getOrderId());
        transactionDetails.put("gross_amount", trxRequest.getGrossAmount());

        params.put("transaction_details", transactionDetails);

        try {
            String token = SnapApi.createTransactionToken(params);
            String redirectUrl = System.getenv("MIDTRANS_API_URL") + token;

            // Save order details
            CustomerOrder customerOrder = new CustomerOrder();

            // Check if user exists
            User user = usersRepository.findById(trxRequest.getUserId())
                    .orElseThrow(() -> new DataNotFoundException("User not found"));

            // Check if warehouse exists
            Warehouse warehouse = warehouseRepository.findById(trxRequest.getWarehouseId())
                    .orElseThrow(() -> new DataNotFoundException("Warehouse not found"));

            // Check if payment method exists
            PaymentMethod paymentMethod = paymentMethodRepository.findById(trxRequest.getPaymentMethodId())
                    .orElseThrow(() -> new DataNotFoundException("Payment method not found"));

            // Check if order status exists
            CustomerOrderStatus customerOrderStatus = customerOrderStatusRepository.findById(trxRequest.getOrderStatusId())
                    .orElseThrow(() -> new DataNotFoundException("Customer order status not found"));

            customerOrder.setUser(user);
            customerOrder.setWarehouse(warehouse);
            customerOrder.setPaymentMethod(paymentMethod);
            customerOrder.setGatewayTrxId(token);
            customerOrder.setShippingCost(trxRequest.getShippingCost());
            customerOrder.setTotalAmount(trxRequest.getGrossAmount());
            customerOrder.setOrderStatus(customerOrderStatus);
            customerOrder.setInvoiceCode(randomOrderId);

            customerOrder = customerOrderRepository.save(customerOrder);

            // Save order items
            CustomerOrder finalCustomerOrder = customerOrder;

            trxRequest.getOrderItems().forEach(item -> {
                if (item.getProductId() == null) {
                    throw new IllegalArgumentException("Product ID must not be null for order item.");
                }

                CustomerOrderItem orderItem = new CustomerOrderItem();

                // Check if product exists
                Product product = productRepository.findById(item.getProductId())
                        .orElseThrow(() -> new DataNotFoundException("Product not found"));

                orderItem.setCustomerOrder(finalCustomerOrder);
                orderItem.setProduct(product);
                orderItem.setQuantity(item.getQuantity());
                orderItem.setProductPrice(item.getUnitPrice());

                customerOrderItemsRepository.save(orderItem);
            });

            return new TransactionResponseDTO(token, redirectUrl);

        }  catch (MidtransError e) {
            throw new RuntimeException("Midtrans error: " + e.getMessage(), e);
        }
    }
}
