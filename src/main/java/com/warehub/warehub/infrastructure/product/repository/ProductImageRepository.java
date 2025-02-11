package com.warehub.warehub.infrastructure.product.repository;

import com.warehub.warehub.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByProductIdAndDeletedAtIsNull( Long productId);

    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.id = :productId AND pi.position = 1 AND pi.deletedAt IS NULL")
    Optional<ProductImage> findMainImageByProductId(@Param("productId") Long productId);

    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.id IN :productIds AND pi.position = 1 AND pi.deletedAt IS NULL")
    List<ProductImage> findMainImagesByProductIds(@Param("productIds") Set<Long> productIds);

}
