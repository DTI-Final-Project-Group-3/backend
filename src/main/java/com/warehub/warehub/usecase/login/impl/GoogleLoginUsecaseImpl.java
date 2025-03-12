package com.warehub.warehub.usecase.login.impl;

import com.warehub.warehub.entity.Oauth2UserInfo;
import com.warehub.warehub.entity.User;
import com.warehub.warehub.entity.enums.RoleType;
import com.warehub.warehub.infrastructure.login.dto.GoogleLoginRequestDTO;
import com.warehub.warehub.infrastructure.login.dto.LoginResponseDTO;
import com.warehub.warehub.infrastructure.login.dto.UserAuth;
import com.warehub.warehub.infrastructure.login.repository.Oauth2UserInfoRepository;
import com.warehub.warehub.infrastructure.users.repository.RolesRepository;
import com.warehub.warehub.infrastructure.users.repository.UsersRepository;
import com.warehub.warehub.usecase.login.GoogleLoginUsecase;
import com.warehub.warehub.usecase.user.TokenGenerationUsecase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Service
public class GoogleLoginUsecaseImpl implements GoogleLoginUsecase {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private TokenGenerationUsecase tokenGenerationUsecase;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private Oauth2UserInfoRepository oauth2UserInfoRepository;

    private static final String GOOGLE_TOKEN_INFO_URL = "https://oauth2.googleapis.com/tokeninfo?access_token=";

    @Value("${google.client.id}")
    private String GOOGLE_CLIENT_ID;

    public boolean verifyGoogleToken(String accessToken, String expectedClientId) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.getForEntity(GOOGLE_TOKEN_INFO_URL + accessToken, Map.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                return false;
            }

            Map<String, Object> body = response.getBody();
            if (body == null || !body.containsKey("aud")) {
                return false;
            }

            String audience = (String) body.get("aud");
            return expectedClientId.equals(audience); // Pastikan token berasal dari aplikasi kita
        } catch (Exception e) {
            return false; // Jika terjadi error, anggap token tidak valid
        }
    }

    @Transactional
    @Override
    public LoginResponseDTO login(GoogleLoginRequestDTO requestDTO) {

        if (!verifyGoogleToken(requestDTO.getAccessToken(), GOOGLE_CLIENT_ID)) {
            throw new RuntimeException("Invalid Google access token!");
        }

        System.out.println("Google login token berhasil diverifikasi");

        Optional<User> optionalUser = usersRepository.findByEmailIgnoreCase(requestDTO.getEmail());
        User user;
        String email;
        if (optionalUser.isEmpty()) {
            // Create a new user if not exists
            user = new User();
            user.setEmail(requestDTO.getEmail());
            user.setFullname(requestDTO.getName());
            user.setProfileImageUrl(requestDTO.getProfilePictureUrl());
            user.setRole(rolesRepository.findByName(RoleType.NOT_VERIFIED.toString()).get()); // Assign default role
            user = usersRepository.save(user);

            email = requestDTO.getEmail();

        } else {
            user = optionalUser.get();
            email = user.getEmail();
        }

        Optional<Oauth2UserInfo> oauth2UserInfo = oauth2UserInfoRepository.findByUserId(user.getId());
        Oauth2UserInfo info;
        if (oauth2UserInfo.isPresent()) {
            info = oauth2UserInfo.get();
        } else {
            info = new Oauth2UserInfo();
        }

        info.setUser(user);
        info.setAccessToken(requestDTO.getAccessToken());
        info.setProvider(requestDTO.getProvider());
        info.setProviderUserId(requestDTO.getProviderUserId());
        oauth2UserInfoRepository.save(info);

        String scope = new UserAuth(user).getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .reduce((a, b) -> a + " " + b)
                .orElse("");

        String accessToken = tokenGenerationUsecase.generateToken(email, scope,3600L);
        String refreshToken = tokenGenerationUsecase.generateToken(email, scope, 604800L);
        long expiresAt = Instant.now().plusSeconds(3600L).toEpochMilli();
        long refreshExpiresAt = Instant.now().plusSeconds(604800L).toEpochMilli();
        return new LoginResponseDTO(accessToken,refreshToken,expiresAt,refreshExpiresAt, user.getRole().getName());
    }
}
