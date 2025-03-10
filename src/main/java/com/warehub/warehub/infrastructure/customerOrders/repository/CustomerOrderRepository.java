package com.warehub.warehub.infrastructure.customerOrders.repository;

import com.warehub.warehub.entity.CustomerOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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

    List<CustomerOrder> findByPaymentMethodId(Long paymentMethodId);
}
