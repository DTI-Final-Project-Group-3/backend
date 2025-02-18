package com.warehub.warehub.usecase.transaction;

import com.warehub.warehub.infrastructure.transaction.dto.ManualTransactionRequestDTO;
import com.warehub.warehub.infrastructure.transaction.dto.ManualTransactionResponseDTO;

public interface ManualTransactionUsecase {
    ManualTransactionResponseDTO createManualTransaction(ManualTransactionRequestDTO request);
}
