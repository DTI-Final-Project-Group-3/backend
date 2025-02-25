package com.warehub.warehub.usecase.transaction;

import com.warehub.warehub.infrastructure.transaction.dto.GatewayTransactionRequestDTO;
import com.warehub.warehub.infrastructure.transaction.dto.GatewayTransactionResponseDTO;

public interface GatewayTransactionUsecase {
    GatewayTransactionResponseDTO createGatewayTransaction(GatewayTransactionRequestDTO trxRequest);
}
