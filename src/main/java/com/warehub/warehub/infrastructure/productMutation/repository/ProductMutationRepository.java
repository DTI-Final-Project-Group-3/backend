package com.warehub.warehub.infrastructure.productMutation.repository;

import com.warehub.warehub.entity.ProductMutation;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationDailySummaryResponseDTO;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationDetailResponseDTO;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationReportResponseDTO;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationTotalResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductMutationRepository extends JpaRepository<ProductMutation, Long> {
    Optional<ProductMutation> findByIdAndDeletedAtIsNull(Long productMutationId);

    @Query(value = """
    SELECT
        pm.id AS id,
        pm.created_at AS createdAt,
        pm.quantity AS quantity,
        p.id AS productId,
        p.name AS productName,
        pc.id AS productCategoryId,
        pc.name AS productCategoryName,
        pmt.id AS productMutationTypeId,
        pmt.name AS productMutationTypeName,
        pms.id AS productMutationStatusId,
        pms.name AS productMutationStatusName
    FROM product_mutations pm
    JOIN products p ON pm.product_id = p.id
    JOIN product_categories pc ON p.product_category_id = pc.id
    JOIN product_mutation_types pmt ON pm.product_mutation_type_id = pmt.id
    JOIN product_mutation_statuses pms ON pm.product_mutation_status_id = pms.id
    WHERE
        pm.deleted_at IS NULL
        AND pm.created_at >= :startedAt
        AND pm.created_at <= :endedAt
        AND (:productId IS NULL OR pm.product_id = :productId)
        AND (:productCategoryId IS NULL OR p.product_category_id = :productCategoryId)
        AND (:productMutationTypeId IS NULL OR pm.product_mutation_type_id = :productMutationTypeId)
        AND (:productMutationStatusId IS NULL OR pm.product_mutation_status_id = :productMutationStatusId)
    ORDER BY pm.created_at DESC
    """, nativeQuery = true)
    List<ProductMutationReportResponseDTO> findProductMutationDetailsByDateRange(
            @Param("startedAt") OffsetDateTime startedAt,
            @Param("endedAt") OffsetDateTime endedAt,
            @Param("productId") Long productId,
            @Param("productCategoryId") Long productCategoryId,
            @Param("productMutationTypeId") Long productMutationTypeId,
            @Param("productMutationStatusId") Long productMutationStatusId
    );

    @Query(nativeQuery = true, value = """
        WITH date_series AS (
          SELECT generate_series(
            :startedAt,
            :endedAt,
            '1 day'::interval
          )::date AS date
        )
        SELECT
          ds.date as date,
          COALESCE(SUM(CASE WHEN pm.quantity > 0 THEN pm.quantity ELSE 0 END), 0) AS total_addition,
          COALESCE(SUM(CASE WHEN pm.quantity < 0 THEN ABS(pm.quantity) ELSE 0 END), 0) AS total_reduction
        FROM
          date_series ds
        LEFT JOIN
          product_mutations pm ON ds.date = pm.created_at::date
        LEFT JOIN
          products p ON pm.product_id = p.id
        WHERE
          (pm.id IS NULL OR
          ((:productId IS NULL OR pm.product_id = :productId)
          AND (:productCategoryId IS NULL OR p.product_category_id = :productCategoryId)
          AND (:productMutationTypeId IS NULL OR pm.product_mutation_type_id = :productMutationTypeId)
          AND (:productMutationStatusId IS NULL OR pm.product_mutation_status_id = :productMutationStatusId)
          AND (pm.created_at BETWEEN :startedAt AND :endedAt)))
        GROUP BY
          ds.date
        ORDER BY
          ds.date DESC
        """)
    List<ProductMutationDailySummaryResponseDTO> findDailyMutationSummary(
            @Param("startedAt") OffsetDateTime startedAt,
            @Param("endedAt") OffsetDateTime endedAt,
            @Param("productId") Long productId,
            @Param("productCategoryId") Long productCategoryId,
            @Param("productMutationTypeId") Long productMutationTypeId,
            @Param("productMutationStatusId") Long productMutationStatusId);

    @Query(value = """
    SELECT
        SUM(CASE WHEN pm.created_at < :startedAt THEN pm.quantity ELSE 0 END) AS starting_quantity,
        SUM(CASE WHEN pm.created_at BETWEEN :startedAt AND :endedAt
            AND pm.quantity > 0 THEN pm.quantity ELSE 0 END) AS total_added,
        ABS(SUM(CASE WHEN pm.created_at BETWEEN :startedAt AND :endedAt 
            AND pm.quantity < 0 THEN pm.quantity ELSE 0 END)) AS total_reduced,
        SUM(CASE WHEN pm.created_at BETWEEN :startedAt AND :endedAt THEN pm.quantity ELSE 0 END) AS net_change,
        SUM(CASE WHEN pm.created_at <= :endedAt THEN pm.quantity ELSE 0 END) AS ending_quantity
    FROM product_mutations pm
    JOIN products p ON pm.product_id = p.id
    WHERE
        pm.deleted_at IS NULL
        AND (:productId IS NULL OR pm.product_id = :productId)
        AND (:productCategoryId IS NULL OR p.product_category_id = :productCategoryId)
        AND (:productMutationTypeId IS NULL OR pm.product_mutation_type_id = :productMutationTypeId)
        AND (:productMutationStatusId IS NULL OR pm.product_mutation_status_id = :productMutationStatusId)
            """, nativeQuery = true)
    ProductMutationTotalResponseDTO calculateProductQuantityMetricsByDateRange( @Param("startedAt") OffsetDateTime startedAt,
                                                            @Param("endedAt") OffsetDateTime endedAt,
                                                            @Param("productId") Long productId,
                                                            @Param("productCategoryId") Long productCategoryId,
                                                            @Param("productMutationTypeId") Long productMutationTypeId,
                                                            @Param("productMutationStatusId") Long productMutationStatusId);

    @Query(value = """
    SELECT *
    FROM product_mutations pm
    WHERE
        pm.deleted_at IS NULL
        AND pm.product_mutation_status_id = 1
        AND pm.created_at + CAST(:expiryInterval AS INTERVAL) < :now
    """, nativeQuery = true)
    List<ProductMutation> findPendingExpired(@Param("now") OffsetDateTime now, @Param("expiryInterval") String expiryInterval);


    @Query(value = """
                SELECT 
                    pm.id AS productMutationId,
                    p.id AS productId,
                    p.name AS productName,
                    pi.url AS productThumbnail,
                    pm.quantity AS quantity,
                    u1.id AS requesterId,
                    u1.fullname AS requesterName,
                    pm.requester_notes AS requesterNotes,
                    u2.id AS reviewerId,
                    u2.fullname AS reviewerName,
                    pm.reviewer_notes AS reviewerNotes,
                    w1.id AS originWarehouseId,
                    w1.name AS originWarehouseName,
                    w2.id AS destinationWarehouseId,
                    w2.name AS destinationWarehouseName,
                    pmt.id AS productMutationTypeId,
                    pmt.name AS productMutationTypeName,
                    pms.id AS productMutationStatusId,
                    pms.name AS productMutationStatusName,
                    pm.invoice_code AS invoiceCode,
                    pm.created_at AS createdAt,
                    pm.reviewed_at AS reviewedAt
                FROM product_mutations pm
                JOIN products p ON pm.product_id = p.id
                LEFT JOIN product_images pi ON pi.product_id = p.id AND pi.position = 1
                LEFT JOIN users u1 ON pm.requester_id = u1.id
                LEFT JOIN users u2 ON pm.reviewer_id = u2.id
                LEFT JOIN warehouses w1 ON pm.origin_warehouse_id = w1.id
                LEFT JOIN warehouses w2 ON pm.destination_warehouse_id = w2.id
                JOIN product_mutation_types pmt ON pm.product_mutation_type_id = pmt.id
                JOIN product_mutation_statuses pms ON pm.product_mutation_status_id = pms.id
                WHERE pm.deleted_at IS NULL 
                  AND pm.product_mutation_type_id IN :productMutationTypeId
                  AND (:originWarehouseId IS NULL OR pm.origin_warehouse_id = :originWarehouseId)
                  AND (:destinationWarehouseId IS NULL OR pm.destination_warehouse_id = :destinationWarehouseId)
                ORDER BY pm.created_at DESC
            """, nativeQuery = true)
    Page<ProductMutationDetailResponseDTO> findByWarehouseIdDTO(@Param("originWarehouseId") Long originWarehouseId,
                                                                @Param("destinationWarehouseId") Long destinationWarehouseId,
                                                                @Param("productMutationTypeId") List<Long> productMutationTypeId,
                                                                Pageable pageable);
}
