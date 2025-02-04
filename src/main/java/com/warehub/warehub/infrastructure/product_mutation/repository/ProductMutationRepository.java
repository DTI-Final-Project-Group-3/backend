package com.warehub.warehub.infrastructure.product_mutation.repository;

import com.warehub.warehub.entity.ProductMutation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductMutationRepository extends JpaRepository<ProductMutation, Long> {
    Optional<ProductMutation> findByIdAndDeletedAtIsNull(Long productMutationId);
}
