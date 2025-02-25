package com.warehub.warehub.infrastructure.transaction.controller;

import com.warehub.warehub.common.response.ApiResponse;
import com.warehub.warehub.infrastructure.security.Claims;
import com.warehub.warehub.infrastructure.transaction.dto.*;
import com.warehub.warehub.usecase.transaction.ManualTransactionUsecase;
import com.warehub.warehub.usecase.transaction.GatewayTransactionUsecase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {
    private final GatewayTransactionUsecase transactionUsecase;
    private final ManualTransactionUsecase manualTransactionUsecase;

    public TransactionController(GatewayTransactionUsecase transactionUsecase, ManualTransactionUsecase manualTransactionUsecase) {
        this.transactionUsecase = transactionUsecase;
        this.manualTransactionUsecase = manualTransactionUsecase;
    }

    @PostMapping("/create-gateway")
    public ResponseEntity<?> createTransaction(@RequestBody GatewayTransactionRequestDTO trxRequest) {
        Long userId = Claims.getUserIdFromJwt();

        GatewayTransactionRequestDTO trxRequestDTO = new GatewayTransactionRequestDTO();
        trxRequestDTO.setUserId(userId);
        trxRequestDTO.setLatitude(trxRequest.getLatitude());
        trxRequestDTO.setLongitude(trxRequest.getLongitude());
        trxRequestDTO.setGrossAmount(trxRequest.getGrossAmount());
        trxRequestDTO.setOrderStatusId(trxRequest.getOrderStatusId());
        trxRequestDTO.setPaymentMethodId(trxRequest.getPaymentMethodId());
        trxRequestDTO.setShippingCost(trxRequest.getShippingCost());
        trxRequestDTO.setOrderItems(trxRequest.getOrderItems());

        GatewayTransactionResponseDTO trxResponse = transactionUsecase.createGatewayTransaction(trxRequestDTO);

        return ApiResponse.successfulResponse("Gateway transaction created successfully.", trxResponse);
    }

    @PostMapping("/create-manual")
    public ResponseEntity<?> createManualTransaction(@RequestBody ManualTransactionRequestDTO trxRequest) {
        Long userId = Claims.getUserIdFromJwt();

        ManualTransactionRequestDTO trxRequestDTO = new ManualTransactionRequestDTO();
        trxRequestDTO.setUserId(userId);
        trxRequestDTO.setLatitude(trxRequest.getLatitude());
        trxRequestDTO.setLongitude(trxRequest.getLongitude());
        trxRequestDTO.setGrossAmount(trxRequest.getGrossAmount());
        trxRequestDTO.setOrderStatusId(trxRequest.getOrderStatusId());
        trxRequestDTO.setPaymentMethodId(trxRequest.getPaymentMethodId());
        trxRequestDTO.setShippingCost(trxRequest.getShippingCost());
        trxRequestDTO.setOrderItems(trxRequest.getOrderItems());

        ManualTransactionResponseDTO trxResponse = manualTransactionUsecase.createManualTransaction(trxRequestDTO);

        return ApiResponse.successfulResponse("Manual transaction created successfully.", trxResponse);
    }

    @PutMapping("/payment-proof/{customerOrderId}")
    public ResponseEntity<?> updateManualPaymentImage(@PathVariable("customerOrderId") Long customerOrderId, UpdatePaymentProofRequestDTO req) {
        Long userId = Claims.getUserIdFromJwt();

        UpdatePaymentProofRequestDTO reqDTO = new UpdatePaymentProofRequestDTO();
        reqDTO.setUserId(userId);
        reqDTO.setPaymentProofImage(req.getPaymentProofImage());

        return ApiResponse.successfulResponse("Upload payment proof image successfully.", manualTransactionUsecase.updateManualPaymentProof(customerOrderId, req));
    }
}
