package com.warehub.warehub.infrastructure.productMutation.repository;

import com.warehub.warehub.entity.ProductMutation;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationDailySummaryResponseDTO;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationDetailResponseDTO;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationHistoryResponseDTO;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationTotalResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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
        AND (CAST(:startedAt AS DATE) IS NULL OR CAST(:endedAt AS DATE) IS NULL OR (pm.created_at::date BETWEEN CAST(:startedAt AS DATE) AND CAST(:endedAt AS DATE)))
        AND (:warehouseId IS NULL 
            OR (pm.product_mutation_type_id = 6 AND pm.origin_warehouse_id = :warehouseId)
            OR (pm.product_mutation_type_id = 3 AND pm.destination_warehouse_id = :warehouseId)
            OR (pm.product_mutation_type_id = 4 AND pm.destination_warehouse_id = :warehouseId)
            OR (pm.product_mutation_type_id = 5 AND pm.destination_warehouse_id = :warehouseId)
            OR (pm.product_mutation_type_id = 1 AND pm.destination_warehouse_id = :warehouseId))
        AND (CAST(:productId AS BIGINT) IS NULL OR pm.product_id = CAST(:productId AS BIGINT))
        AND (CAST(:productCategoryId AS BIGINT) IS NULL OR p.product_category_id = CAST(:productCategoryId AS BIGINT))
        AND (CAST(:productMutationTypeId AS BIGINT) IS NULL OR pm.product_mutation_type_id = CAST(:productMutationTypeId AS BIGINT))
        AND (CAST(:productMutationStatusId AS BIGINT) IS NULL OR pm.product_mutation_status_id = CAST(:productMutationStatusId AS BIGINT))
    ORDER BY pm.created_at DESC
    """, nativeQuery = true)
    Page<ProductMutationHistoryResponseDTO> findProductMutationDetailsByDateRange(
            @Param("startedAt") LocalDate startedAt,
            @Param("endedAt") LocalDate endedAt,
            @Param("productId") Long productId,
            @Param("productCategoryId") Long productCategoryId,
            @Param("productMutationTypeId") Long productMutationTypeId,
            @Param("productMutationStatusId") Long productMutationStatusId,
            @Param("warehouseId") Long warehouseId,
            Pageable pageable
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
          product_mutations pm 
          ON ds.date = pm.created_at::date
        AND (:warehouseId IS NULL 
            OR (pm.product_mutation_type_id = 6 AND pm.origin_warehouse_id = :warehouseId)
            OR (pm.product_mutation_type_id = 3 AND pm.destination_warehouse_id = :warehouseId)
            OR (pm.product_mutation_type_id = 4 AND pm.destination_warehouse_id = :warehouseId)
            OR (pm.product_mutation_type_id = 5 AND pm.destination_warehouse_id = :warehouseId)
            OR (pm.product_mutation_type_id = 1 AND pm.destination_warehouse_id = :warehouseId))
          AND (:productId IS NULL OR pm.product_id = :productId)
          AND (:productMutationTypeId IS NULL OR pm.product_mutation_type_id = :productMutationTypeId)
          AND (:productMutationStatusId IS NULL OR pm.product_mutation_status_id = :productMutationStatusId)
        LEFT JOIN
          products p 
          ON pm.product_id = p.id
          AND (:productCategoryId IS NULL OR p.product_category_id = :productCategoryId)
        GROUP BY
          ds.date
        ORDER BY
          ds.date ASC
    """)
    List<ProductMutationDailySummaryResponseDTO> findDailyMutationSummary(
            @Param("startedAt") LocalDate startedAt,
            @Param("endedAt") LocalDate endedAt,
            @Param("productId") Long productId,
            @Param("productCategoryId") Long productCategoryId,
            @Param("productMutationTypeId") Long productMutationTypeId,
            @Param("productMutationStatusId") Long productMutationStatusId,
            @Param("warehouseId") Long warehouseId);

    @Query(value = """
    SELECT
        SUM(CASE WHEN pm.created_at::date < :startedAt THEN pm.quantity ELSE 0 END) AS starting_quantity,
        SUM(CASE WHEN pm.created_at::date BETWEEN :startedAt AND :endedAt
            AND pm.quantity > 0 THEN pm.quantity ELSE 0 END) AS total_added,
        ABS(SUM(CASE WHEN pm.created_at::date BETWEEN :startedAt AND :endedAt 
            AND pm.quantity < 0 THEN pm.quantity ELSE 0 END)) AS total_reduced,
        SUM(CASE WHEN pm.created_at::date BETWEEN :startedAt AND :endedAt THEN pm.quantity ELSE 0 END) AS net_change,
        SUM(CASE WHEN pm.created_at::date <= :endedAt THEN pm.quantity ELSE 0 END) AS ending_quantity
    FROM product_mutations pm
    JOIN products p ON pm.product_id = p.id
    WHERE
        pm.deleted_at IS NULL
        AND (:warehouseId IS NULL 
            OR (pm.product_mutation_type_id = 6 AND pm.origin_warehouse_id = :warehouseId)
            OR (pm.product_mutation_type_id = 3 AND pm.destination_warehouse_id = :warehouseId)
            OR (pm.product_mutation_type_id = 4 AND pm.destination_warehouse_id = :warehouseId)
            OR (pm.product_mutation_type_id = 5 AND pm.destination_warehouse_id = :warehouseId)
            OR (pm.product_mutation_type_id = 1 AND pm.destination_warehouse_id = :warehouseId))
        AND (:productId IS NULL OR pm.product_id = :productId)
        AND (:productCategoryId IS NULL OR p.product_category_id = :productCategoryId)
        AND (:productMutationTypeId IS NULL OR pm.product_mutation_type_id = :productMutationTypeId)
        AND (:productMutationStatusId IS NULL OR pm.product_mutation_status_id = :productMutationStatusId)
            """, nativeQuery = true)
    ProductMutationTotalResponseDTO calculateProductQuantityMetricsByDateRange( @Param("startedAt") LocalDate startedAt,
                                                            @Param("endedAt") LocalDate endedAt,
                                                            @Param("productId") Long productId,
                                                            @Param("productCategoryId") Long productCategoryId,
                                                            @Param("productMutationTypeId") Long productMutationTypeId,
                                                            @Param("productMutationStatusId") Long productMutationStatusId,
                                                            @Param("warehouseId") Long warehouseId);

    @Query(value = """
    SELECT *
    FROM product_mutations pm
    WHERE
        pm.deleted_at IS NULL
        AND pm.reviewed_at IS NULL
        AND pm.product_mutation_status_id = 1
        AND (pm.product_mutation_type_id = 1 OR pm.product_mutation_type_id = 6)
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
            AND (
                (CAST(:startDate AS DATE) IS NULL OR CAST(:endDate AS DATE) IS NULL)
                OR
                (pm.created_at::timestamptz::date BETWEEN CAST(:startDate AS DATE) AND CAST(:endDate AS DATE))
            )
      AND (:isRequest = FALSE OR pm.reviewed_at IS NULL)
      AND (:productMutationTypeId IS NULL OR pm.product_mutation_type_id IN :productMutationTypeId)
      AND (:productMutationStatusId IS NULL OR pm.product_mutation_status_id = :productMutationStatusId)
      AND (:originWarehouseId IS NULL OR pm.origin_warehouse_id = :originWarehouseId)
      AND (:destinationWarehouseId IS NULL OR pm.destination_warehouse_id = :destinationWarehouseId)
      AND (:productId IS NULL OR pm.product_id = :productId)
      AND (:productCategoryId IS NULL OR p.product_category_id = :productCategoryId)
    ORDER BY pm.created_at DESC
    """, nativeQuery = true)
    Page<ProductMutationDetailResponseDTO> findByWarehouseIdDTO(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("isRequest") boolean isRequest,
            @Param("productId") Long productId,
            @Param("productCategoryId") Long productCategoryId,
            @Param("originWarehouseId") Long originWarehouseId,
            @Param("destinationWarehouseId") Long destinationWarehouseId,
            @Param("productMutationTypeId") List<Long> productMutationTypeId,
            @Param("productMutationStatusId") Long productMutationStatusId,
            Pageable pageable);
    List<ProductMutation> findByInvoiceCodeAndProductId(String invoiceCode, Long productId);


    @Query(value = """
        SELECT CASE
                   WHEN COUNT(pm) > 0 THEN TRUE
                   ELSE FALSE
               END
        FROM product_mutations pm
        LEFT JOIN products p ON p.id = pm.product_id
        WHERE pm.deleted_at IS NULL
          AND pm.product_mutation_status_id = :productMutationStatusId
          AND p.product_category_id = :productCategoryId
        """, nativeQuery = true)
    boolean existPendingMutationByProductCategoryId(@Param("productMutationStatusId")Long productMutationStatusId,
                                     @Param("productCategoryId") Long productCategoryId);

    @Query(value = """
        SELECT CASE
                   WHEN COUNT(pm) > 0 THEN TRUE
                   ELSE FALSE
               END
        FROM product_mutations pm
        WHERE pm.deleted_at IS NULL
          AND pm.product_mutation_status_id = :productMutationStatusId
          AND pm.product_id = :productId
        """, nativeQuery = true)
    boolean existPendingMutationByProductId(@Param("productMutationStatusId")Long productMutationStatusId,
                                                    @Param("productId") Long productId);


    List<ProductMutation> findByProductMutationCodeAndDeletedAtIsNull(String productMutationCode);


}
