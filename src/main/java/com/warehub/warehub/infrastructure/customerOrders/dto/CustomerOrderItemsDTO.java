package com.warehub.warehub.infrastructure.customerOrders.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerOrderItemsDTO {
    private Long id;
    private Long orderId;
    private Long productId;
    private Integer quantity;
    private BigDecimal unitPrice;
}
