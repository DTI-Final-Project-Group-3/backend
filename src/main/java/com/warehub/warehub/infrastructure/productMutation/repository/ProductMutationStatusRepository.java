package com.warehub.warehub.infrastructure.productMutation.repository;

import com.warehub.warehub.entity.ProductMutationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductMutationStatusRepository extends JpaRepository<ProductMutationStatus, Long> {
    Optional<ProductMutationStatus> findByIdAndDeletedAtIsNull(Long productMutationStatusId);
}
