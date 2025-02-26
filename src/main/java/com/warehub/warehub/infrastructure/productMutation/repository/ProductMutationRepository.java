package com.warehub.warehub.infrastructure.productMutation.repository;

import com.warehub.warehub.entity.ProductMutation;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationDetailResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductMutationRepository extends JpaRepository<ProductMutation, Long> {
    Optional<ProductMutation> findByIdAndDeletedAtIsNull(Long productMutationId);

    @Query(value = """
                SELECT 
                    pm.id AS productMutationId,
                    p.id AS productId,
                    p.name AS productName,
                    pi.url AS productThumbnail,
                    pm.quantity AS quantity,
                    pm.notes AS notes,
                    u1.id AS requesterId,
                    u1.fullname AS requesterName,
                    u2.id AS approverId,
                    u2.fullname AS approverName,
                    w1.id AS originWarehouseId,
                    w1.name AS originWarehouseName,
                    w2.id AS destinationWarehouseId,
                    w2.name AS destinationWarehouseName,
                    pmt.id AS productMutationTypeId,
                    pmt.name AS productMutationTypeName,
                    pms.id AS productMutationStatusId,
                    pms.name AS productMutationStatusName,
                    pm.created_at AS createdAt,
                    pm.accepted_at AS acceptedAt
                FROM product_mutations pm
                JOIN products p ON pm.product_id = p.id
                LEFT JOIN product_images pi ON pi.product_id = p.id AND pi.position = 1
                LEFT JOIN users u1 ON pm.requester_id = u1.id
                LEFT JOIN users u2 ON pm.approver_id = u2.id
                LEFT JOIN warehouses w1 ON pm.origin_warehouse_id = w1.id
                LEFT JOIN warehouses w2 ON pm.destination_warehouse_id = w2.id
                JOIN product_mutation_types pmt ON pm.product_mutation_type_id = pmt.id
                JOIN product_mutation_statuses pms ON pm.product_mutation_status_id = pms.id
                WHERE pm.deleted_at IS NULL 
                  AND pm.product_mutation_type_id = :productMutationTypeId
                  AND (:originWarehouseId IS NULL OR pm.origin_warehouse_id = :originWarehouseId)
                  AND (:destinationWarehouseId IS NULL OR pm.destination_warehouse_id = :destinationWarehouseId)
                ORDER BY pm.created_at
            """, nativeQuery = true)
    Page<ProductMutationDetailResponseDTO> findByWarehouseIdDTO(@Param("originWarehouseId") Long originWarehouseId,
                                                                @Param("destinationWarehouseId") Long destinationWarehouseId,
                                                                @Param("productMutationTypeId") Long productMutationTypeId,
                                                                Pageable pageable);
}
