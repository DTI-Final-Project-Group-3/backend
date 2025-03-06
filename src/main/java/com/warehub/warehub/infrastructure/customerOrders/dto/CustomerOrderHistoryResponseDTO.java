package com.warehub.warehub.infrastructure.customerOrders.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerOrderHistoryResponseDTO {

    private OffsetDateTime dateTime;
    private String invoiceCode;
    private Long orderId;
    private Long orderStatusId;
    private String orderStatusName;
    private Long orderItemId;
    private Long productId;
    private String productName;
    private Long productCategoryId;
    private String productCategoryName;
    private Integer quantity;
    private BigDecimal unitPrice;
}
