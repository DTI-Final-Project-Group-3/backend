package com.warehub.warehub.infrastructure.customerOrderItems.repository;

import com.warehub.warehub.entity.CustomerOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerOrderItemsRepository extends JpaRepository<CustomerOrderItem, Long> {
    List<CustomerOrderItem> findByCustomerOrderId(Long customerOrderId);
}
