package com.warehub.warehub.infrastructure.productMutation.repository;

import com.warehub.warehub.entity.ProductMutationStatus;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationStatusResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductMutationStatusRepository extends JpaRepository<ProductMutationStatus, Long> {
    Optional<ProductMutationStatus> findByIdAndDeletedAtIsNull(Long productMutationStatusId);
    Optional<ProductMutationStatus> findByNameIgnoreCaseAndDeletedAtIsNull(String productMutationStatusName);

    @Query(value = """
                SELECT
                    pms.id,
                    pms.name
                FROM product_mutation_statuses pms
                WHERE
                    pms.deleted_at IS NULL
               """, nativeQuery = true)
    List<ProductMutationStatusResponseDTO> findAllAndDeletedAtIsNull();
}
