package com.warehub.warehub.infrastructure.warehouse_inventories.repository;

import com.warehub.warehub.entity.WarehouseInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseInventoryRepository extends JpaRepository<WarehouseInventory, Long> {

    Optional<WarehouseInventory> findByProductIdAndWarehouseIdAndDeletedAtIsNull(Long productId, Long warehouseId);

    boolean existsByProductIdAndWarehouseIdAndDeletedAtIsNull(Long productId, Long warehouseId);

    Optional<WarehouseInventory> findByIdAndDeletedAtIsNull(Long warehouseInventoryId);

    List<WarehouseInventory> findByWarehouseIdAndDeletedAtIsNull(Long warehouseId);
}
