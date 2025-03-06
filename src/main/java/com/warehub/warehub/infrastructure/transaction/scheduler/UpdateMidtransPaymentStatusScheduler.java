package com.warehub.warehub.infrastructure.transaction.scheduler;

import com.warehub.warehub.common.exceptions.DataNotFoundException;
import com.warehub.warehub.entity.CustomerOrder;
import com.warehub.warehub.entity.CustomerOrderStatus;
import com.warehub.warehub.entity.enums.OrderStatuses;
import com.warehub.warehub.infrastructure.customerOrderStatus.CustomerOrderStatusRepository;
import com.warehub.warehub.infrastructure.customerOrders.repository.CustomerOrderRepository;
import com.warehub.warehub.usecase.transaction.impl.MidtransService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@Service
public class UpdateMidtransPaymentStatusScheduler {
    private final MidtransService midtransService;
    private final CustomerOrderRepository customerOrderRepository;
    private final ObjectMapper objectMapper;
    private final CustomerOrderStatusRepository customerOrderStatusRepository;

    public UpdateMidtransPaymentStatusScheduler(MidtransService midtransService, CustomerOrderRepository customerOrderRepository, ObjectMapper objectMapper, CustomerOrderStatusRepository customerOrderStatusRepository) {
        this.midtransService = midtransService;
        this.customerOrderRepository = customerOrderRepository;
        this.objectMapper = objectMapper;
        this.customerOrderStatusRepository = customerOrderStatusRepository;
    }

    @Scheduled(fixedRate = 60000) // every 1 minute
    @Transactional
    public void updateCustomerOrderStatus() {
        List<CustomerOrder> customerOrders = customerOrderRepository.findByPaymentMethodId(1L);
        System.out.println("customer orders lists : "+customerOrders);

        System.out.println("Schedule task : Checking Midtrans payment statuses...");

        for (CustomerOrder order : customerOrders) {
            try {
                // Extract response body from ResponseEntity<String>
                String response = midtransService.getPaymentStatus(order.getInvoiceCode()).getBody();
                JsonNode jsonResponse = objectMapper.readTree(response);

                String status = jsonResponse.get("transaction_status").asText();
                if (status == null) {
                    System.out.println("Midtrans response missing transaction_status for order: " + order.getInvoiceCode());
                    continue;
                }

                CustomerOrderStatus paymentSettled = customerOrderStatusRepository.findById(OrderStatuses.PROCESSED.getId())
                        .orElseThrow(() -> new DataNotFoundException("Customer order status not found"));

                CustomerOrderStatus paymentExpired = customerOrderStatusRepository.findById(OrderStatuses.CANCELED.getId())
                        .orElseThrow(() -> new DataNotFoundException("Customer order status not found"));

                if ("settlement".equals(status)) {
                    order.setOrderStatus(paymentSettled);
                    customerOrderRepository.save(order);
                    System.out.println("Order " + order.getInvoiceCode() + " updated to PROCESSED");
                } else if ("expired".equals(status)) {
                    order.setOrderStatus(paymentExpired);
                    customerOrderRepository.save(order);
                    System.out.println("Order " + order.getInvoiceCode() + " updated to CANCELED");
                }
            } catch (Exception e) {
//                System.out.println("Error updating order " + order.getInvoiceCode() + ": " + e.getMessage());
            }
        }
        System.out.println("Schedule task : Checking Midtrans payment statuses completed");
    }
}
