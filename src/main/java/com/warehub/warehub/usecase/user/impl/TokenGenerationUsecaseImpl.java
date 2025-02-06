package com.warehub.warehub.usecase.user.impl;

import com.warehub.warehub.common.exceptions.DataNotFoundException;
import com.warehub.warehub.entity.User;
import com.warehub.warehub.infrastructure.users.repository.UsersRepository;
import com.warehub.warehub.usecase.user.TokenGenerationUsecase;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TokenGenerationUsecaseImpl implements TokenGenerationUsecase {
    private final JwtEncoder jwtEncoder;
    private final UsersRepository usersRepository;

    public TokenGenerationUsecaseImpl(JwtEncoder jwtEncoder, UsersRepository usersRepository) {
        this.jwtEncoder = jwtEncoder;
        this.usersRepository = usersRepository;
    }

    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();

        // 10 hours
        long expiry = 36000L;

        String email = authentication.getName();

        User user = usersRepository.findByEmailContainsIgnoreCase(email)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .reduce((a, b) -> a + " " + b)
                .orElse("");

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(email)
                .claim("scope", scope)
                .claim("userId", user.getId())
                .claim("userEmail", user.getEmail())
                .claim("userRole", user.getRole().getName())
                .build();

        JwsHeader jwsHeader = JwsHeader.with(() -> "RS256").build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }
}