package com.warehub.warehub.infrastructure.warehouse.repository;

import com.warehub.warehub.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    @Query(value = "SELECT * FROM warehouses w WHERE ST_DistanceSphere(w.location, ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)) <= :distance AND w.deleted_at IS NULL ORDER BY ST_DistanceSphere(w.location, ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)) ASC", nativeQuery = true)
    List<Warehouse> findActiveNearbyWarehouses(@Param("lng") double lng, @Param("lat") double lat, @Param("distance") double distance);

    @Query("SELECT w FROM Warehouse w WHERE w.deletedAt IS NULL")
    List<Warehouse> findActiveAll();

    @Query("SELECT w FROM Warehouse w WHERE LOWER(w.name) = LOWER(:name) AND w.deletedAt IS NULL")
    Optional<Warehouse> findActiveByNameIgnoreCase(String name);

    @Query("SELECT w FROM Warehouse w WHERE w.id = :id AND w.deletedAt IS NULL")
    Optional<Warehouse> findActiveById(@Param("id") Long id);

}
