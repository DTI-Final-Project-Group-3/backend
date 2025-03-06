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

    @Scheduled(cron = "0 */3 * * * *") // every 3 minutes
    public void scheduleUpdateCustomerOrderStatus() {
        System.out.println("Scheduled task started : Checking customer order statuses...");
        adminCustomerOrderUsecase.autoUpdateCustomerOrderStatus();
        System.out.println("Scheduled task completed : Customer order statuses updated.");
    }
}
