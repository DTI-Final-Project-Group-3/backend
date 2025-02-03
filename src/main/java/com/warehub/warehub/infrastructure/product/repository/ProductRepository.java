package com.warehub.warehub.infrastructure.product.repository;

import com.warehub.warehub.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @Query("SELECT p FROM Product p WHERE p.deletedAt IS NULL")
    List<Product> findActiveAll();

    @Query("SELECT p FROM Product p WHERE p.id = :productId AND p.deletedAt IS NULL")
    Optional<Product> findActiveById(@Param("productId") Long productId);

    @Query ("SELECT p FROM Product p WHERE p.productCategory.id = :productCategoryId AND p.deletedAt IS NULL")
    List<Product> findActiveByProductCategoryId(@Param("productCategoryId") Long productCategoryId);
}

