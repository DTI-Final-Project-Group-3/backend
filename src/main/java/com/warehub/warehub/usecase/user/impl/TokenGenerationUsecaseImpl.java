package com.warehub.warehub.usecase.user.impl;

import com.warehub.warehub.common.exceptions.DataNotFoundException;
import com.warehub.warehub.entity.User;
import com.warehub.warehub.infrastructure.users.repository.UsersRepository;
import com.warehub.warehub.usecase.user.TokenGenerationUsecase;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;

@Service
public class TokenGenerationUsecaseImpl implements TokenGenerationUsecase {
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final UsersRepository usersRepository;

    public TokenGenerationUsecaseImpl(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder, UsersRepository usersRepository) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
        this.usersRepository = usersRepository;
    }

    public String generateToken(String email, String scope, long expiry) {

        User user = usersRepository.findByEmailContainsIgnoreCase(email)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        Instant now = Instant.now();

        String name = Objects.requireNonNullElse(user.getFullname(), "");
        String profilePictureUrl = Objects.requireNonNullElse(user.getProfileImageUrl(), "");

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(email)
                .claim("userId", user.getId())
                .claim("scope", scope)
                .claim("name", name)
                .claim("profilePictureUrl", profilePictureUrl)
                .claim("userRole", user.getRole().getName())
                .build();

        JwsHeader jwsHeader = JwsHeader.with(() -> "RS256").build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    public String generateToken(Authentication authentication, long expiry) {
        String email = authentication.getName();

        String scope = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .reduce((a, b) -> a + " " + b)
                .orElse("");

        return generateToken(email, scope, expiry);
    }

    // Token verification method
    public boolean tokenVerify(String token) {
        try {
            Jwt decodedJwt = jwtDecoder.decode(token);
            Instant expiration = decodedJwt.getExpiresAt();

            // Ensure the token is not expired
            return expiration.isAfter(Instant.now());
        } catch (JwtException e) {
            return false; // Invalid token (expired or tampered with)
        }
    }

    public String extractEmailFromToken(String token) {
        Jwt decodedJwt = jwtDecoder.decode(token);
        return decodedJwt.getSubject(); // Assuming email is the subject
    }

    public String extractScopeFromToken(String token) {
        Jwt decodedJwt = jwtDecoder.decode(token);
        return decodedJwt.getClaimAsString("scope"); // Extract the "scope" claim
    }
}
