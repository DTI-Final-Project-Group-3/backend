package com.warehub.warehub.infrastructure.customerOrders.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerOrderDailyTotalResponseDTO {

    private LocalDate date;
    private Long totalQuantity;
    private Long totalValue;
}
