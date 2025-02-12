package com.warehub.warehub.usecase.user;

import org.springframework.security.core.Authentication;

public interface TokenGenerationUsecase {
    String generateToken(Authentication authentication, long expiry);
    String generateToken(String email, String scope, long expiry);
    boolean tokenVerify(String token);
    String extractEmailFromToken(String token);
    String extractScopeFromToken(String token);
}