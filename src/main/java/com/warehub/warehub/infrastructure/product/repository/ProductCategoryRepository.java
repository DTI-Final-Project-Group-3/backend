package com.warehub.warehub.infrastructure.product.repository;

import com.warehub.warehub.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    @Query("SELECT p FROM ProductCategory p WHERE LOWER(p.name) = LOWER(:name) AND p.deletedAt IS NULL")
    Optional<ProductCategory> findActiveByNameIgnoreCase(@Param("name") String name);

    @Query("SELECT p FROM ProductCategory p WHERE p.id = :id AND p.deletedAt IS NULL")
    Optional<ProductCategory> findActiveById(@Param("id") Long id);

    @Query("SELECT p FROM ProductCategory p WHERE p.deletedAt IS NULL")
    List<ProductCategory> findActiveAll();
}
