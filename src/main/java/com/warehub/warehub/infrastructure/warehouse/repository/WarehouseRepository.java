package com.warehub.warehub.infrastructure.warehouse.repository;

import com.warehub.warehub.entity.Warehouse;
import com.warehub.warehub.infrastructure.warehouse.dto.NearbyWarehouseQuantityResponseDTO;
import com.warehub.warehub.infrastructure.warehouse.dto.WarehouseResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long>, JpaSpecificationExecutor<Warehouse> {

    @Query(value = """
            SELECT w.id, w.name, 
                ST_X(w.location) AS longitude, 
                ST_Y(w.location) AS latitude, 
                ST_DistanceSphere(w.location, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)) AS distance
            FROM warehouses w
            JOIN warehouse_inventories wi ON wi.warehouse_id = w.id
            WHERE 
                (:radius IS NULL OR ST_DistanceSphere(w.location, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)) <= :radius)
                AND w.deleted_at IS NULL
                AND w.deleted_at IS NULL 
                AND (:productId IS NULL OR wi.product_id = :productId)
            GROUP BY w.id, w.name, longitude, latitude, distance
            ORDER BY distance ASC
        """, nativeQuery = true)
    List<Object[]> findNearbyWarehousesByCoordinate(
            @Param("longitude") double longitude,
            @Param("latitude") double latitude,
            @Param("radius") double radius,
            @Param("productId") Long productId);

    @Query(value = """
    SELECT 
         w.id, 
         w.name, 
         ST_DistanceSphere(
             w.location, 
             ST_SetSRID(ST_MakePoint(coords.longitude, coords.latitude), 4326)
         ) AS distance,
         wi.quantity
    FROM warehouses w
    JOIN warehouse_inventories wi ON wi.warehouse_id = w.id
    CROSS JOIN (
        SELECT 
            ST_X(location) AS longitude, 
            ST_Y(location) AS latitude
        FROM warehouses
        WHERE deleted_at IS NULL
          AND id = :warehouseId
    ) coords
    WHERE w.deleted_at IS NULL 
      AND wi.deleted_at IS NULL
      AND wi.quantity > 0
      AND wi.warehouse_id != :warehouseId
      AND (:productId IS NULL OR wi.product_id = :productId)
    ORDER BY distance ASC
""", nativeQuery = true)
    List<NearbyWarehouseQuantityResponseDTO> findNearbyWarehouseByWarehouseIdAndProductId(
            @Param("warehouseId") Long warehouseID,
            @Param("productId") Long productId);

    @Query(value = """
            SELECT 
                w.id,
                w.name
            FROM warehouses w
            JOIN warehouse_inventories wi ON wi.warehouse_id = w.id
            WHERE
                w.deleted_at IS NULL
            AND (:radius IS NULL OR ST_DWithin(
                w.location::geography, 
                ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography, 
                :radius))
            AND wi.deleted_at IS NULL
            AND wi.product_id = :productId
            AND wi.quantity > 0
            """, nativeQuery = true)
    Optional<WarehouseResponseDTO> findNearestWarehouseByProductId(@Param("longitude") Double longitude,
                                                                   @Param("latitude") Double latitude,
                                                                   @Param("radius") Double radius,
                                                                   @Param("productId") Long productId);

    @Query(value = """
            SELECT w.id, w.name, 
                ST_X(w.location) AS longitude, 
                ST_Y(w.location) AS latitude, 
                ST_DistanceSphere(w.location, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)) AS distance
            FROM warehouses w
            JOIN warehouse_inventories wi ON wi.warehouse_id = w.id
            WHERE ST_DistanceSphere(w.location, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)) <= :radius 
                AND w.deleted_at IS NULL 
            GROUP BY w.id, w.name, longitude, latitude, distance
            ORDER BY distance ASC
        """, nativeQuery = true)
    List<Object[]> findNearestWarehouses(
            @Param("longitude") double longitude,
            @Param("latitude") double latitude,
            @Param("radius") double radius);


    List<Warehouse> findAllByDeletedAtIsNull();

    Optional<Warehouse> findByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Optional<Warehouse> findByIdAndDeletedAtIsNull(Long id);


    @Query("""
    SELECT DISTINCT w FROM Warehouse w
    JOIN WarehouseAdmin wa ON wa.warehouse.id = w.id
    WHERE w.deletedAt IS NULL
    """)
    List<Warehouse> findAllWarehousesWithAssignedAdmins();

    @Query("""
    SELECT w FROM Warehouse w
    WHERE w.deletedAt IS NULL
    AND w.id NOT IN (SELECT wa.warehouse.id FROM WarehouseAdmin wa)
    """)
    List<Warehouse> findAllWarehousesWithoutAdmins();
}
