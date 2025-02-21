package com.warehub.warehub.infrastructure.product.repository;

import com.warehub.warehub.entity.ProductImage;
import com.warehub.warehub.infrastructure.product.dto.ProductImageResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    @Query(value = "SELECT pi.url, pi.position FROM product_images pi WHERE pi.product_id = :productId AND pi.deleted_at IS NULL", nativeQuery = true)
    List<ProductImageResponseDTO> findByProductIdAndDeletedAtIsNullDTO(@Param("productId") Long productId);

    List<ProductImage> findByProductIdAndDeletedAtIsNull(@Param("productId") Long productId);

    @Query("SELECT pi.url FROM ProductImage pi WHERE pi.product.id = :productId AND pi.position = 1 AND pi.deletedAt IS NULL")
    String findMainImageUrlByProductId(@Param("productId") Long productId);

    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.id IN :productIds AND pi.position = 1 AND pi.deletedAt IS NULL")
    List<ProductImage> findMainImagesByProductIds(@Param("productIds") List<Long> productIds);

}
