package com.warehub.warehub.infrastructure.warehouse.repository;

import com.warehub.warehub.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
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
            WHERE ST_DistanceSphere(w.location, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)) <= :radius 
                AND w.deleted_at IS NULL 
                AND (:productId IS NULL OR wi.product_id = :productId)
            GROUP BY w.id, w.name, longitude, latitude, distance
            ORDER BY distance ASC
        """, nativeQuery = true)
    List<Object[]> findNearbyWarehouses(
            @Param("longitude") double longitude,
            @Param("latitude") double latitude,
            @Param("radius") double radius,
            @Param("productId") Long productId);

    List<Warehouse> findAllByDeletedAtIsNull();

    Optional<Warehouse> findByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Optional<Warehouse> findByIdAndDeletedAtIsNull(Long id);
}
