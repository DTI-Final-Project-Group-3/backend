package com.warehub.warehub.infrastructure.product.repository;

import com.warehub.warehub.entity.Product;
import com.warehub.warehub.infrastructure.product.dto.ProductBasicResponseDTO;
import com.warehub.warehub.infrastructure.product.dto.ProductSummaryResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    List<Product> findAllByDeletedAtIsNull();

    Optional<Product> findByIdAndDeletedAtIsNull(Long productId);

    Optional<Product> findByNameIgnoreCaseAndDeletedAtIsNull(String name);

    List<Product> findByProductCategoryIdAndDeletedAtIsNull(Long productCategoryId);

    @Query(value = """
            SELECT 
                p.id,
                p.name
           FROM products p
           WHERE p.deleted_at is NULL
           ORDER BY p.id
            """, nativeQuery = true)
    List<ProductBasicResponseDTO> findAllProduct();

    @Query(value = """
            SELECT
                p.id,
                p.name
            FROM products p
            JOIN warehouse_inventories wi ON wi.product_id = p.id
            WHERE
                p.deleted_at IS NULL
                AND wi.deleted_at IS NULL
                AND wi.warehouse_id = :warehouseId
            ORDER BY p.id
            """, nativeQuery = true)
    List<ProductBasicResponseDTO> findProductsIncludeFilter(@Param("warehouseId") Long warehouseId);

    @Query(value = """
            SELECT
                p.id,
                p.name
            FROM products p
            WHERE
                p.deleted_at IS NULL
                AND p.id NOT IN (
                    SELECT wi.product_id
                    FROM warehouse_inventories wi
                    WHERE wi.deleted_at IS NULL
                      AND wi.warehouse_id = :warehouseId
                )
            ORDER BY p.id
            """, nativeQuery = true)
    List<ProductBasicResponseDTO> findProductsExcludeFilter(@Param("warehouseId") Long warehouseId);


    @Query(value = """
    SELECT 
        p.id AS "id",
        p.name AS "name",
        p.price AS "price",
        p.weight AS "weight",
        p.height AS "height",
        p.width AS "width",
        p.length AS "length",
        pc.name AS "category_name",
        pi.url AS "thumbnail",
        SUM(wi.quantity) AS "total_quantity"
    FROM products p
    JOIN product_categories pc ON p.product_category_id = pc.id
    LEFT JOIN product_images pi ON p.id = pi.product_id AND pi.position = 1
    JOIN warehouse_inventories wi ON p.id = wi.product_id
    JOIN warehouses w ON wi.warehouse_id = w.id
    WHERE
        p.deleted_at IS NULL
        AND (:radius IS NULL
             OR ST_DWithin(w.location::geography, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography, :radius))
        AND (:productCategoryId IS NULL OR p.product_category_id = :productCategoryId)
        AND (:searchQuery IS NULL OR p.name ILIKE CONCAT('%', :searchQuery, '%'))
    GROUP BY p.id, p.name, p.price, pc.name, pi.url
    ORDER BY
        CASE 
            WHEN SUM(wi.quantity) > 0 THEN 1
            ELSE 2
        END,
        MIN(ST_DistanceSphere(w.location, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)))
    """, nativeQuery = true)
    Page<ProductSummaryResponseDTO> findPaginatedProductsByUserLocationAndFilter(
            @Param("longitude") Double longitude,
            @Param("latitude") Double latitude,
            @Param("radius") Double radius,
            @Param("productCategoryId") Long productCategoryId,
            @Param("searchQuery") String searchQuery,
            Pageable pageable
    );

    @Query(value = """
    SELECT 
        p.id AS "id",
        p.name AS "name",
        p.price AS "price",
        p.weight AS "weight",
        p.height AS "height",
        p.width AS "width",
        p.length AS "length",
        pc.name AS "category_name",
        pi.url AS "thumbnail"
    FROM products p
    JOIN product_categories pc ON p.product_category_id = pc.id
    LEFT JOIN product_images pi ON p.id = pi.product_id AND pi.position = 1
    WHERE
        p.deleted_at IS NULL
        AND (:productCategoryId IS NULL OR p.product_category_id = :productCategoryId)
        AND (:searchQuery IS NULL OR p.name ILIKE CONCAT('%', :searchQuery, '%'))
    ORDER BY p.id
    """, nativeQuery = true)
    Page<ProductSummaryResponseDTO> findPaginatedProductsByFilter(
            @Param("productCategoryId") Long productCategoryId,
            @Param("searchQuery") String searchQuery,
            Pageable pageable);
}

