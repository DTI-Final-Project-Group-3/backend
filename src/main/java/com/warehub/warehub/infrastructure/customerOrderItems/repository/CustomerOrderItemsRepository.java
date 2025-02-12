package com.warehub.warehub.infrastructure.customerOrderItems.repository;

import com.warehub.warehub.entity.CustomerOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerOrderItemsRepository extends JpaRepository<CustomerOrderItem, Long> {
}
