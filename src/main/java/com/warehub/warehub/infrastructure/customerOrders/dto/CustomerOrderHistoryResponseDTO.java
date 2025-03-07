package com.warehub.warehub.infrastructure.customerOrders.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerOrderHistoryResponseDTO {

    private Instant dateTime;
    private Long orderId;
    private String invoiceCode;
    private Long orderStatusId;
    private String orderStatusName;
    private Long orderItemId;
    private Long productId;
    private String productName;
    private Long productCategoryId;
    private String productCategoryName;
    private Long quantity;
    private BigDecimal unitPrice;
}
