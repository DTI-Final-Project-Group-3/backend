package com.warehub.warehub.infrastructure.warehouse_inventories.repository;

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
                SELECT DISTINCT ON (wi.product_id) wi.* 
                FROM warehouse_inventories wi 
                JOIN products p ON wi.product_id = p.id 
                WHERE 
                  wi.warehouse_id = ANY(CAST(:nearbyWarehouseIds AS bigint[])) 
                  AND (:productCategoryId IS NULL OR p.product_category_id = :productCategoryId) 
                  AND (:searchQuery IS NULL OR p.name ILIKE CONCAT('%', :searchQuery, '%')) 
                ORDER BY 
                  wi.product_id, 
                  array_position(CAST(:nearbyWarehouseIds AS bigint[]), wi.warehouse_id)
            """,
            countQuery = """
                SELECT COUNT(DISTINCT wi.product_id) 
                FROM warehouse_inventories wi 
                JOIN products p ON wi.product_id = p.id 
                WHERE 
                  wi.warehouse_id = ANY(CAST(:nearbyWarehouseIds AS bigint[])) 
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


