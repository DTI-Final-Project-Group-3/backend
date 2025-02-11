package com.warehub.warehub.infrastructure.customerOrderStatus;

import com.warehub.warehub.entity.CustomerOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerOrderStatusRepository extends JpaRepository<CustomerOrderStatus, Integer> {
}
