package com.warehub.warehub.infrastructure.warehouse.repository;

import com.warehub.warehub.entity.Role;
import com.warehub.warehub.entity.User;
import com.warehub.warehub.entity.WarehouseAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WarehouseAdminRepository extends JpaRepository<WarehouseAdmin, Long> {
    Optional<WarehouseAdmin> findByUserAssigneeId(Long userId);
    List<WarehouseAdmin> findByWarehouseId(Long warehouseId);
}
