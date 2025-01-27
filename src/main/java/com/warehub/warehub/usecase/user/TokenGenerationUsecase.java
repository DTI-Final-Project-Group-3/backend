package com.warehub.warehub.usecase.user;

import org.springframework.security.core.Authentication;

public interface TokenGenerationUsecase {
    String generateToken(Authentication authentication);
}