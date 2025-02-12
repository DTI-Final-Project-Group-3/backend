package com.warehub.warehub.infrastructure.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequestDTO {
    private String orderId;
    private BigDecimal grossAmount;

    private Long userId;
    private Long warehouseId;
    private Long paymentMethodId;
    private BigDecimal shippingCost;
    private Integer orderStatusId;
    private List<OrderItemDTO> orderItems;
}
