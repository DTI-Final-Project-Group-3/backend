package com.warehub.warehub.infrastructure.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePaymentProofRequestDTO {
    private Long userId;
    private String paymentProofImage;
}
