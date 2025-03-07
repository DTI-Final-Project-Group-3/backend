package com.warehub.warehub.infrastructure.users.repository;

import com.warehub.warehub.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
    List<UserAddress> findByUserId(Long userId);
    List<UserAddress> findByUserIdOrderByCreatedAtAsc(Long userId);
    Optional<UserAddress> findByUserIdAndIsPrimaryTrue(Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE UserAddress ua SET ua.isPrimary = false WHERE ua.user.id = :userId")
    void unsetOtherPrimaryAddresses(Long userId);
}