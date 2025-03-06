package com.warehub.warehub.infrastructure.customerOrders.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerOrderHistoryRequestDTO {

    private int page;
    private int limit;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long warehouseId;
    private Long customerOrderStatusId;
    private Long productId;
    private Long productCategoryId;
}
