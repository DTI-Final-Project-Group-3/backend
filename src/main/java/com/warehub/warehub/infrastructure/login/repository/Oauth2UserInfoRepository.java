package com.warehub.warehub.infrastructure.login.repository;


import com.warehub.warehub.entity.Oauth2UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Oauth2UserInfoRepository extends JpaRepository<Oauth2UserInfo, Long> {
    Optional<Oauth2UserInfo> findByProviderAndProviderUserId(String provider, String providerUserId);
    Optional<Oauth2UserInfo> findByUserId(Long userId);
}
