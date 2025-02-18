package com.warehub.warehub.infrastructure.customerOrders.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedCustomerOrderRequestDTO {
    private Long userId;

    private int page = 0;
    private int limit = 10;
    private Long customerOrderStatusId;
    private String searchQuery;
}
