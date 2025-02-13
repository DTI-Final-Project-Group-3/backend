package com.warehub.warehub.infrastructure.warehouseInventory.repository;

import com.warehub.warehub.entity.WarehouseInventory;
import com.warehub.warehub.infrastructure.warehouseInventory.dto.WarehouseInventorySummaryResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
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
                SELECT DISTINCT ON (wi.product_id) 
                   wi.id AS warehouse_inventory_id,
                   p.id AS product_id,
                   p.name AS product_name,
                   p.price AS product_price,
                   pc.id AS product_category_id,
                   pc.name AS product_category_name,
                   pi.url AS product_a,
                   wi.quantity AS quantity,
                   wis.id AS warehouse_inventory_status_id,
                   wis.name AS warehouse_inventory_status_name,
                   w.id AS warehouse_id,
                   w.name AS warehouse_name
                FROM warehouse_inventories wi 
                JOIN products p ON wi.product_id = p.id
                JOIN product_categories pc ON pc.id = p.product_category_id
                JOIN warehouses w ON w.id = wi.warehouse_id
                JOIN warehouse_inventory_statuses wis ON wis.id = wi.warehouse_inventory_status_id
                LEFT JOIN product_images pi ON pi.product_id = p.id AND pi.position = 1 AND pi.deleted_at IS NULL 
                WHERE 
                    wi.deleted_at IS NULL
                    AND wis.id IN (1,2)
                    AND w.id = ANY(:nearbyWarehouseIds)
                    AND (:productCategoryId IS NULL OR pc.id = :productCategoryId)
                    AND (:searchQuery IS NULL OR p.name ILIKE CONCAT('%', CAST(:searchQuery AS text), '%'))
                ORDER BY 
                  wi.product_id, 
                  CASE 
                      WHEN wi.warehouse_inventory_status_id = 1 THEN 1 
                      ELSE 2 
                  END,
                  array_position(:nearbyWarehouseIds, wi.warehouse_id)
            ) AS sub 
            ORDER BY 
                CASE 
                    WHEN sub.warehouse_inventory_status_id = 1 THEN 1 
                    ELSE 2 
                END,
                array_position(:nearbyWarehouseIds, sub.warehouse_id)
        """,
            countQuery = """
            SELECT COUNT(DISTINCT wi.product_id) 
            FROM warehouse_inventories wi 
            JOIN products p ON wi.product_id = p.id 
            JOIN product_categories pc ON pc.id = p.product_category_id
            JOIN warehouses w ON w.id = wi.warehouse_id
            JOIN warehouse_inventory_statuses wis ON wis.id = wi.warehouse_inventory_status_id
            LEFT JOIN product_images pi ON pi.product_id = p.id AND pi.position = 1 AND pi.deleted_at IS NULL
            WHERE 
                wi.deleted_at IS NULL
                AND wis.id IN (1,2)
                AND w.id = ANY(:nearbyWarehouseIds)
                AND (:productCategoryId IS NULL OR pc.id = :productCategoryId)
                AND (:searchQuery IS NULL OR p.name ILIKE CONCAT('%', CAST(:searchQuery AS text), '%'))
        """,
            nativeQuery = true)
    Page<WarehouseInventorySummaryResponseDTO> findDistinctByProduct(
            @Param("nearbyWarehouseIds") Long[] nearbyWarehouseIds,
            @Param("productCategoryId") Long productCategoryId,
            @Param("searchQuery") String searchQuery,
            Pageable pageable
    );

}


