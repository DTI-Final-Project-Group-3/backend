package com.warehub.warehub.infrastructure.transaction.repository;

import com.warehub.warehub.entity.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<CustomerOrder, Long> {
}
