package com.warehub.warehub.infrastructure.productMutation.repository;

import com.warehub.warehub.entity.ProductMutationType;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationTypeResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductMutationTypeRepository extends JpaRepository<ProductMutationType, Long> {
    Optional<ProductMutationType> findByIdAndDeletedAtIsNull(Long productMutationTypeId);

    Optional<ProductMutationType> findByNameIgnoreCaseAndDeletedAtIsNull(String manualMutation);

    @Query(value = """
                SELECT
                    pmt.id,
                    pmt.name
                FROM product_mutation_types pmt
                WHERE
                    pmt.deleted_at IS NULL
               """, nativeQuery = true)
    List<ProductMutationTypeResponseDTO> findAllAndDeletedAtIsNull();
}