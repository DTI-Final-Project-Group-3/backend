package com.warehub.warehub.infrastructure.users.repository;

import com.warehub.warehub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailContainsIgnoreCase(String email);
    Optional<User> findByIdAndDeletedAtIsNull(Long userId);
}