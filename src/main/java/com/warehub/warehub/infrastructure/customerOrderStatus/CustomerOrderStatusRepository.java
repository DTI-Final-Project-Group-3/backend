package com.warehub.warehub.infrastructure.customerOrderStatus;

import com.warehub.warehub.entity.CustomerOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface CustomerOrderStatusRepository extends JpaRepository<CustomerOrderStatus, Integer> {
    Optional<CustomerOrderStatus> findByName(String statusName);


}
