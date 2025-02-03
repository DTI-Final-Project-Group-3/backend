package com.warehub.warehub.infrastructure.product.repository;

import com.warehub.warehub.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    @Query("SELECT p FROM ProductImage p WHERE p.id = :productId AND p.deletedAt IS NULL")
    List<ProductImage> findActiveByProductId(@Param("productId") Long productId);
}
