package com.warehub.warehub.infrastructure.warehouseAdmin.repository;

import com.warehub.warehub.entity.WarehouseAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WarehouseAdminRepository extends JpaRepository<WarehouseAdmin, Long> {
    WarehouseAdmin findByUserAssigneeId(Long userId);
}
