package com.warehub.warehub.infrastructure.customerOrders.scheduler;

import com.warehub.warehub.usecase.customerOrder.AdminCustomerOrderUsecase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CustomerOrderScheduler {
    private final AdminCustomerOrderUsecase adminCustomerOrderUsecase;

    public CustomerOrderScheduler(AdminCustomerOrderUsecase adminCustomerOrderUsecase) {
        this.adminCustomerOrderUsecase = adminCustomerOrderUsecase;
    }

    @Scheduled(cron = "0 */2 * * * *") // every 2 minutes
    public void scheduleUpdateCustomerOrderStatus() {
        System.out.println("Scheduled task started : Checking customer order statuses...");

        adminCustomerOrderUsecase.autoUpdateCustomerOrderStatus();
        System.out.println("Customer order statuses updated.");

        adminCustomerOrderUsecase.autoConfirmCustomerOrderStatus();
        System.out.println("Auto update shipped order status into confirmed.");

        System.out.println("Scheduled task completed : Customer order statuses updated.");
    }
}
