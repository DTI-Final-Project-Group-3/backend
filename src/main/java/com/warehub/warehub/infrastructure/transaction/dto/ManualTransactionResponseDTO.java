package com.warehub.warehub.infrastructure.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManualTransactionResponseDTO {
    private Long transactionId;  // Matches `id` from CustomerOrder
    private String orderId;      // Consider using `invoiceCode`

    private BigDecimal grossAmount; // Matches `totalAmount`
    private BigDecimal shippingCost;

    private String paymentProofUrl;
    private Long userId;
    private Long warehouseId;
    private Long paymentMethodId;
    private Integer orderStatusId;

    private List<OrderItemDTO> orderItems;  // Matches `customerOrderItems`

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String transactionStatus; // From `orderStatus`

    private String gatewayTransactionId; // Matches `gatewayTrxId`
}
