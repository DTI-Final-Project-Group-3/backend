package com.warehub.warehub.infrastructure.customerOrders.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerOrderDailyTotalResponseDTO {

    private Date date;
    private Long totalQuantity;
    private BigDecimal totalValue;
}
