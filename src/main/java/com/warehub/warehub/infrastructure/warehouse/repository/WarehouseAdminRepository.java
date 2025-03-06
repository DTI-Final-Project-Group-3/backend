package com.warehub.warehub.infrastructure.warehouse.repository;

import com.warehub.warehub.entity.Role;
import com.warehub.warehub.entity.User;
import com.warehub.warehub.entity.Warehouse;
import com.warehub.warehub.entity.WarehouseAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseAdminRepository extends JpaRepository<WarehouseAdmin, Long> {
    Optional<WarehouseAdmin> findByUserAssigneeId(Long userId);
    List<WarehouseAdmin> findByWarehouseId(Long warehouseId);

    @Query("""
    SELECT wa FROM WarehouseAdmin wa
    JOIN FETCH wa.warehouse w
    WHERE w.deletedAt IS NULL
    """)
    List<WarehouseAdmin> findAllWarehouseAdminsWithWarehouse();
}
