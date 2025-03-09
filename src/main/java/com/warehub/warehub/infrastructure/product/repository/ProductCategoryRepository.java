package com.warehub.warehub.infrastructure.product.repository;

import com.warehub.warehub.entity.ProductCategory;
import com.warehub.warehub.infrastructure.product.dto.ProductCategoryResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    Optional<ProductCategory> findByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Optional<ProductCategory> findByIdAndDeletedAtIsNull(Long id);

    List<ProductCategory> findAllByDeletedAtIsNullOrderByIdAsc();

    @Query(value = """
            SELECT
                pc.id,
                pc.name
            FROM product_categories pc
            WHERE pc.deleted_at IS NULL
            ORDER BY pc.id
            """, nativeQuery = true)
    Page<ProductCategoryResponseDTO> findPaginatedProductCategories(Pageable pageable);
}
