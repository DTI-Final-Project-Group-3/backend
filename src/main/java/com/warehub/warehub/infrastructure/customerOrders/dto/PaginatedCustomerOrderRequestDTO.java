package com.warehub.warehub.infrastructure.customerOrders.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedCustomerOrderRequestDTO {

    private Long userId;
    private Long warehouseId;
    private int page = 0;
    private int limit = 10;
    private Long customerOrderStatusId;
    private String searchQuery;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;

}
