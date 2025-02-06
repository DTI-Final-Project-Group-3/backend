package com.warehub.warehub.usecase.transaction.impl;

import com.midtrans.Midtrans;
import com.midtrans.httpclient.SnapApi;
import com.midtrans.httpclient.error.MidtransError;
import com.warehub.warehub.infrastructure.customerOrders.repository.CustomerOrderRepository;
import com.warehub.warehub.infrastructure.transaction.dto.TransactionRequestDTO;
import com.warehub.warehub.infrastructure.transaction.dto.TransactionResponseDTO;
import com.warehub.warehub.usecase.transaction.TransactionUsecase;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TransactionUsecaseImpl implements TransactionUsecase {
    private final CustomerOrderRepository customerOrderRepository;

    public TransactionUsecaseImpl(CustomerOrderRepository customerOrderRepository) {
        this.customerOrderRepository = customerOrderRepository;
    }

    // Set serverKey to Midtrans global config
    static {
        Midtrans.serverKey =  System.getenv("MIDTRANS_SERVER_KEY");
        Midtrans.isProduction = false;
    }

//    @Override
//    public TransactionResponseDTO createTransaction(TransactionRequestDTO trxRequest) {
//        // Create map to hold all the parameters for API request
//        Map<String, Object> params = new HashMap<>();
//
//        // Set transaction details
//        Map<String, Object> transactionDetails = new HashMap<>();
//        transactionDetails.put("order_id", trxRequest.getTransactionDetails().getOrderId());
//        transactionDetails.put("gross_amount", trxRequest.getTransactionDetails().getGrossAmount());
//        params.put("transaction_details", transactionDetails);
//
//        // Set credit card details
//        Map<String, Object> creditCard = new HashMap<>();
//        creditCard.put("secure", trxRequest.getCreditCard().isSecure());
//        params.put("credit_card", creditCard);
//
//        // Set item details
//        params.put("item_details", trxRequest.getItemDetails());
//
//        // Set customer details
//        Map<String, Object> customerDetails = new HashMap<>();
//        customerDetails.put("first_name", trxRequest.getCustomerDetails().getFirstName());
//        customerDetails.put("last_name", trxRequest.getCustomerDetails().getLastName());
//        customerDetails.put("email", trxRequest.getCustomerDetails().getEmail());
//        customerDetails.put("phone", trxRequest.getCustomerDetails().getPhone());
//
//        // Set billing and shipping addresses
//        Map<String, Object> billingAddress = new HashMap<>();
//        billingAddress.put("first_name", trxRequest.getCustomerDetails().getBillingAddress().getFirstName());
//        billingAddress.put("last_name", trxRequest.getCustomerDetails().getBillingAddress().getLastName());
//        billingAddress.put("email", trxRequest.getCustomerDetails().getBillingAddress().getEmail());
//        billingAddress.put("phone", trxRequest.getCustomerDetails().getBillingAddress().getPhone());
//        billingAddress.put("address", trxRequest.getCustomerDetails().getBillingAddress().getAddress());
//        billingAddress.put("city", trxRequest.getCustomerDetails().getBillingAddress().getCity());
//        billingAddress.put("postal_code", trxRequest.getCustomerDetails().getBillingAddress().getPostalCode());
//        billingAddress.put("country_code", trxRequest.getCustomerDetails().getBillingAddress().getCountryCode());
//        customerDetails.put("billing_address", billingAddress);
//
//        Map<String, Object> shippingAddress = new HashMap<>();
//        shippingAddress.put("first_name", trxRequest.getCustomerDetails().getShippingAddress().getFirstName());
//        shippingAddress.put("last_name", trxRequest.getCustomerDetails().getShippingAddress().getLastName());
//        shippingAddress.put("email", trxRequest.getCustomerDetails().getShippingAddress().getEmail());
//        shippingAddress.put("phone", trxRequest.getCustomerDetails().getShippingAddress().getPhone());
//        shippingAddress.put("address", trxRequest.getCustomerDetails().getShippingAddress().getAddress());
//        shippingAddress.put("city", trxRequest.getCustomerDetails().getShippingAddress().getCity());
//        shippingAddress.put("postal_code", trxRequest.getCustomerDetails().getShippingAddress().getPostalCode());
//        shippingAddress.put("country_code", trxRequest.getCustomerDetails().getShippingAddress().getCountryCode());
//        customerDetails.put("shipping_address", shippingAddress);
//
//        params.put("customer_details", customerDetails);
//
//        try {
//            String token = SnapApi.createTransactionToken(params);
//            String redirectUrl = "https://app.sandbox.midtrans.com/snap/v2/vtweb/" + token;
//            return new TransactionResponseDTO(token, redirectUrl);
//        } catch (MidtransError e) {
//            throw new RuntimeException("Midtrans error: " + e.getMessage(), e);
//        }
//    }

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
            String redirectUrl = "https://app.sandbox.midtrans.com/snap/v2/vtweb/" + token;

            return new TransactionResponseDTO(token, redirectUrl);

        }  catch (MidtransError e) {
            throw new RuntimeException("Midtrans error: " + e.getMessage(), e);
        }
    }
}
