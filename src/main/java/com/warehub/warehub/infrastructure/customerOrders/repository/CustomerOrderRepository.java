package com.warehub.warehub.infrastructure.customerOrders.repository;

import com.warehub.warehub.entity.CustomerOrder;
import com.warehub.warehub.infrastructure.customerOrders.dto.CustomerOrderHistoryResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long>, JpaSpecificationExecutor<CustomerOrder> {

    @Query("SELECT c FROM CustomerOrder c LEFT JOIN FETCH c.customerOrderitems WHERE "
            + "(:userId IS NULL OR c.user.id = :userId) "
            + "AND (:statusId IS NULL OR c.orderStatus.id = :statusId) "
            + "AND (:search IS NULL OR c.invoiceCode IS NOT NULL AND LOWER(c.invoiceCode) LIKE LOWER(CONCAT('%', :search, '%'))) "
            + "AND (:startDate IS NULL OR c.createdAt >= :startDate) "
            + "AND (:endDate IS NULL OR c.createdAt <= :endDate)")
    Page<CustomerOrder> findAllByFilters(
            @Param("userId") Long userId,
            @Param("statusId") Long statusId,
            @Param("search") String search,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {
            "customerOrderitems",
            "customerOrderitems.product",
            "user",
            "orderStatus"
    })
    Page<CustomerOrder> findAll(Specification<CustomerOrder> spec, Pageable pageable);

    CustomerOrder findByIdAndUserId(Long customerOrderId, Long userId);

    List<CustomerOrder> findByOrderStatusId(Long orderStatusId);

    List<CustomerOrder> findByOrderStatusIdAndSentAtBefore(Long orderStatusId, OffsetDateTime sentAt);

    @Query(
            value = """
        SELECT
          co.created_at AS date_time, 
          co.id AS order_id,          
          co.invoice_code AS invoice_code,
          co.order_status_id AS order_status_id,
          cos.name AS order_status_name,  
          coi.id AS order_item_id,
          coi.product_id AS product_id,
          p.name AS product_name,
          pc.id AS product_category_id,
          pc.name AS product_category_name,
          coi.quantity AS quantity,
          coi.product_price AS unit_price  
        FROM customer_orders co
        JOIN customer_order_status cos ON cos.id = co.order_status_id
        JOIN customer_order_items coi ON coi.customer_order_id = co.id
        JOIN products p ON p.id = coi.product_id
        JOIN product_categories pc ON pc.id = p.product_category_id
        WHERE
          co.created_at::date BETWEEN :startedAt AND :endedAt
          AND (:warehouseId IS NULL OR co.warehouse_id = :warehouseId)
          AND (:customerOrderStatusId IS NULL OR co.order_status_id = :customerOrderStatusId)
          AND (:productId IS NULL OR coi.product_id = :productId)
          AND (:productCategoryId IS NULL OR p.product_category_id = :productCategoryId)
        """,
            countQuery = """
        SELECT COUNT(co.id)
        FROM customer_orders co
        JOIN customer_order_items coi ON coi.customer_order_id = co.id
        JOIN products p ON p.id = coi.product_id
        WHERE
          co.created_at::date BETWEEN :startedAt AND :endedAt
          AND (:warehouseId IS NULL OR co.warehouse_id = :warehouseId)
          AND (:customerOrderStatusId IS NULL OR co.order_status_id = :customerOrderStatusId)
          AND (:productId IS NULL OR coi.product_id = :productId)
          AND (:productCategoryId IS NULL OR p.product_category_id = :productCategoryId)
        """,
            nativeQuery = true
    )
    Page<CustomerOrderHistoryResponseDTO> findHistoryCustomerOrderByFilter(
            @Param("startedAt") LocalDate startedAt,
            @Param("endedAt") LocalDate endedAt,
            @Param("warehouseId") Long warehouseId,
            @Param("customerOrderStatusId") Long customerOrderStatusId,
            @Param("productId") Long productId,
            @Param("productCategoryId") Long productCategoryId,
            Pageable pageable
    );
}
