package com.warehub.warehub.usecase.transaction.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class MidtransService {
    private static final String MIDTRANS_SERVER_KEY = System.getenv("MIDTRANS_SERVER_KEY");
    private static final String MIDTRANS_API_URL = "https://api.sandbox.midtrans.com/v2/";

    public ResponseEntity<String> getPaymentStatus(String transactionId) {
        RestTemplate restTemplate = new RestTemplate();
        String url = MIDTRANS_API_URL + transactionId + "/status";

        // Create authentication header
        String authString = Base64.getEncoder().encodeToString((MIDTRANS_SERVER_KEY + ":").getBytes());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + authString);
        headers.set("Accept", "application/json");
        headers.set("Content-Type", "application/json");

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        // Call Midtrans API
        try {
            return restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error fetching transaction status: " + e.getMessage());
        }
    }
}
