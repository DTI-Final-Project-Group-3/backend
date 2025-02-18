package com.warehub.warehub.infrastructure.transaction.controller;

import com.warehub.warehub.common.response.ApiResponse;
import com.warehub.warehub.infrastructure.transaction.dto.ManualTransactionRequestDTO;
import com.warehub.warehub.infrastructure.transaction.dto.ManualTransactionResponseDTO;
import com.warehub.warehub.infrastructure.transaction.dto.TransactionRequestDTO;
import com.warehub.warehub.infrastructure.transaction.dto.TransactionResponseDTO;
import com.warehub.warehub.usecase.transaction.ManualTransactionUsecase;
import com.warehub.warehub.usecase.transaction.TransactionUsecase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {
    private final TransactionUsecase transactionUsecase;
    private final ManualTransactionUsecase manualTransactionUsecase;

    public TransactionController(TransactionUsecase transactionUsecase, ManualTransactionUsecase manualTransactionUsecase) {
        this.transactionUsecase = transactionUsecase;
        this.manualTransactionUsecase = manualTransactionUsecase;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTransaction(@RequestBody TransactionRequestDTO trxRequest) {
        TransactionResponseDTO trxResponse = transactionUsecase.createTransaction(trxRequest);
        return ApiResponse.successfulResponse("Transaction created successfully.", trxResponse);
    }

    @PostMapping("/create-manual")
    public ResponseEntity<?> createManualTransaction(@RequestBody ManualTransactionRequestDTO trxRequest) {
        ManualTransactionResponseDTO trxResponse = manualTransactionUsecase.createManualTransaction(trxRequest);
        return ApiResponse.successfulResponse("Manual transaction created successfully.", trxResponse);
    }
}
