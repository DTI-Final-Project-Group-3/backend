package com.warehub.warehub.usecase.transaction;

import com.warehub.warehub.infrastructure.transaction.dto.TransactionRequestDTO;
import com.warehub.warehub.infrastructure.transaction.dto.TransactionResponseDTO;

public interface TransactionUsecase {
    TransactionResponseDTO createTransaction(TransactionRequestDTO trxRequest);
}
