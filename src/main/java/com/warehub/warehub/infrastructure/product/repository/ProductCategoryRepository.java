package com.warehub.warehub.infrastructure.product.repository;

import com.warehub.warehub.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    Optional<ProductCategory> findByNameIgnoreCaseAndDeletedAtIsNull(String name);

    Optional<ProductCategory> findByIdAndDeletedAtIsNull(Long id);

    List<ProductCategory> findAllByDeletedAtIsNull();
}
