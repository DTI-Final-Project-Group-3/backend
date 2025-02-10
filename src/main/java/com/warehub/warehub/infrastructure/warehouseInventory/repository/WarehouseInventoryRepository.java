package com.warehub.warehub.infrastructure.warehouseInventory.repository;

import com.warehub.warehub.entity.WarehouseInventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseInventoryRepository extends JpaRepository<WarehouseInventory, Long>, JpaSpecificationExecutor<WarehouseInventory> {

    Optional<WarehouseInventory> findByProductIdAndWarehouseIdAndDeletedAtIsNull(Long productId, Long warehouseId);

    boolean existsByProductIdAndWarehouseIdAndDeletedAtIsNull(Long productId, Long warehouseId);

    Optional<WarehouseInventory> findByIdAndDeletedAtIsNull(Long warehouseInventoryId);

    List<WarehouseInventory> findByWarehouseIdAndDeletedAtIsNull(Long warehouseId);

    @Query(value = """
            SELECT * 
            FROM (
                SELECT DISTINCT ON (wi.product_id) wi.* 
                FROM warehouse_inventories wi 
                JOIN products p ON wi.product_id = p.id 
                WHERE 
                  wi.deleted_at IS NULL
                  AND wi.warehouse_inventory_status_id IN (1,2)
                  AND wi.warehouse_id = ANY(CAST(:nearbyWarehouseIds AS bigint[])) 
                  AND (:productCategoryId IS NULL OR p.product_category_id = :productCategoryId) 
                  AND (:searchQuery IS NULL OR p.name ILIKE CONCAT('%', :searchQuery, '%')) 
                ORDER BY 
                  wi.product_id, 
                  CASE 
                      WHEN wi.warehouse_inventory_status_id = 1 THEN 1 
                      ELSE 2 
                  END,
                  array_position(CAST(:nearbyWarehouseIds AS bigint[]), wi.warehouse_id)
            ) AS sub 
            ORDER BY 
                CASE 
                    WHEN sub.warehouse_inventory_status_id = 1 THEN 1 
                    ELSE 2 
                END,
                array_position(CAST(:nearbyWarehouseIds AS bigint[]), sub.warehouse_id)
        """,
                    countQuery = """
            SELECT COUNT(DISTINCT wi.product_id) 
            FROM warehouse_inventories wi 
            JOIN products p ON wi.product_id = p.id 
            WHERE 
              wi.deleted_at IS NULL
              AND wi.warehouse_inventory_status_id IN (1,2)
              AND wi.warehouse_id = ANY(CAST(:nearbyWarehouseIds AS bigint[])) 
              AND (:productCategoryId IS NULL OR p.product_category_id = :productCategoryId) 
              AND (:searchQuery IS NULL OR p.name ILIKE CONCAT('%', :searchQuery, '%')) 
        """,
            nativeQuery = true)
    Page<WarehouseInventory> findDistinctByProduct(
            @Param("nearbyWarehouseIds") String nearbyWarehouseIds,
            @Param("productCategoryId") Long productCategoryId,
            @Param("searchQuery") String searchQuery,
            Pageable pageable
    );


}


