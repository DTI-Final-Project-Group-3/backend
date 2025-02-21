package com.warehub.warehub.infrastructure.customerOrders.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerOrderDetailRequestDTO {
    Long userId;
    Long orderId;
}
