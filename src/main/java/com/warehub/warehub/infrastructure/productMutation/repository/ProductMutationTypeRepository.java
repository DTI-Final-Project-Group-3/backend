package com.warehub.warehub.infrastructure.productMutation.repository;

import com.warehub.warehub.entity.ProductMutationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductMutationTypeRepository extends JpaRepository<ProductMutationType, Long> {
    Optional<ProductMutationType> findByIdAndDeletedAtIsNull(Long productMutationTypeId);

    Optional<ProductMutationType> findByNameIgnoreCaseAndDeletedAtIsNull(String manualMutation);
}