package com.warehub.warehub.infrastructure.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GatewayTransactionRequestDTO {
    private BigDecimal grossAmount;
    private Double latitude;
    private Double longitude;
    private Long userId;
    private Long paymentMethodId;
    private BigDecimal shippingCost;
    private Integer orderStatusId;
    private List<OrderItemDTO> orderItems;
}
