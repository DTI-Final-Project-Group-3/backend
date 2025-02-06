package com.warehub.warehub.infrastructure.product.repository;

import com.warehub.warehub.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    List<Product> findAllByDeletedAtIsNull();

    Optional<Product> findByIdAndDeletedAtIsNull(Long productId);

    Optional<Product> findByNameIgnoreCaseAndDeletedAtIsNull(String name);

    List<Product> findByProductCategoryIdAndDeletedAtIsNull(Long productCategoryId);
}

