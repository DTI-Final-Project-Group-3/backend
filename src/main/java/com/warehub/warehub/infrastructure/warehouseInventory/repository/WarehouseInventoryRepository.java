package com.warehub.warehub.infrastructure.warehouseInventory.repository;

import com.warehub.warehub.entity.WarehouseInventory;
import com.warehub.warehub.infrastructure.warehouseInventory.dto.WarehouseInventoryPaginationResponseDTO;
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

    boolean existsByProductIdAndWarehouseIdAndDeletedAtIsNull(Long productId, Long warehouseId);

    Optional<WarehouseInventory> findByIdAndDeletedAtIsNull(Long warehouseInventoryId);

    Optional<WarehouseInventory> findByProductIdAndWarehouseIdAndDeletedAtIsNull(Long productId, Long warehouseId);

    @Query(value = """
    SELECT 
        COALESCE(SUM(wi.quantity), 0)
    FROM warehouse_inventories wi
    JOIN warehouses w ON wi.warehouse_id = w.id
    WHERE
        wi.deleted_at IS NULL
        AND wi.product_id = :productId
        AND (:radius IS NULL OR ST_DWithin(
            w.location::geography, 
            ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography, 
            :radius
        ))
    """, nativeQuery = true)
    Integer findTotalStockNearby(@Param("longitude") Double longitude,
                                 @Param("latitude") Double latitude,
                                 @Param("radius") Double radius,
                                 @Param("productId") Long productId);


    @Query(value = """
            SELECT 
                wi.id,
                p.id,
                p.name,
                p.price,
                pc.id,
                pc.name,
                pi.url,
                wi.quantity
            FROM
            warehouse_inventories wi
            JOIN products p ON wi.product_id = p.id
            JOIN product_categories pc ON p.product_category_id = pc.id
            LEFT JOIN product_images pi ON pi.product_id = p.id AND pi.position = 1
            WHERE
                wi.deleted_at IS NULL
                AND wi.warehouse_id = :warehouseId
                AND (:productCategoryId IS NULL OR p.product_category_id = :productCategoryId)
                AND (:searchQuery IS NULL OR p.name ILIKE CONCAT('%', :searchQuery, '%'))
            ORDER BY wi.id
            """, nativeQuery = true)
    Page<WarehouseInventoryPaginationResponseDTO> findByWarehouseId(@Param("warehouseId") Long warehouseId,
                                                                    @Param("productCategoryId") Long productCategoryId,
                                                                    @Param("searchQuery") String searchQuery,
                                                                    Pageable pageable);

    @Query("SELECT wi FROM WarehouseInventory wi " +
            "JOIN wi.product p " +
            "WHERE wi.deletedAt IS NULL " +
            "AND p.productCategory.id = :productCategoryId")
    List<WarehouseInventory> findByProductCategoryId(@Param("productCategoryId") Long productCategoryId);

    List<WarehouseInventory> findByProductIdAndDeletedAtIsNull(Long productId);

}

