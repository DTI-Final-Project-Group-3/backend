package com.warehub.warehub.infrastructure.customerOrders.repository;

import com.warehub.warehub.entity.CustomerOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {

    @Query("SELECT c FROM CustomerOrder c LEFT JOIN FETCH c.customerOrderitems WHERE "
            + "(:userId IS NULL OR c.user.id = :userId) "
            + "AND (:statusId IS NULL OR c.orderStatus.id = :statusId) "
            + "AND (:search IS NULL OR LOWER(c.invoiceCode) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<CustomerOrder> findAllByFilters(
            @Param("userId") Long userId,
            @Param("statusId") Long statusId,
            @Param("search") String search,
            Pageable pageable
    );

}
