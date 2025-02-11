package com.warehub.warehub.usecase.user;

import com.warehub.warehub.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface EmailVerificationUsecase {
    ResponseEntity<?> send(Long userId);
    ResponseEntity<?> verify(String token);
}
