package com.warehub.warehub.usecase.transaction;

import com.warehub.warehub.infrastructure.customerOrders.dto.CustomerOrderResponseDTO;
import com.warehub.warehub.infrastructure.transaction.dto.ManualTransactionRequestDTO;
import com.warehub.warehub.infrastructure.transaction.dto.ManualTransactionResponseDTO;
import com.warehub.warehub.infrastructure.transaction.dto.UpdatePaymentProofRequestDTO;
import com.warehub.warehub.infrastructure.transaction.dto.UpdatePaymentProofResponseDTO;

public interface ManualTransactionUsecase {
    ManualTransactionResponseDTO createManualTransaction(ManualTransactionRequestDTO request);

    UpdatePaymentProofResponseDTO updateManualPaymentProof(Long customerOrderId, UpdatePaymentProofRequestDTO request);

}
