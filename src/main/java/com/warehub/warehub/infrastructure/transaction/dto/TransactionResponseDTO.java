package com.warehub.warehub.infrastructure.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponseDTO {
    private String token;
    private String redirectUrl;

//    private TransactionDetailsDTO details;
}
