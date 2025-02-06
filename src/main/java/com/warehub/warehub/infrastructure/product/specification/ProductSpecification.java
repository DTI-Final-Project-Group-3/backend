package com.warehub.warehub.infrastructure.product.specification;

import com.warehub.warehub.entity.Product;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {

    public static Specification<Product> notDeleted() {
        return (root, query, criteriaBuilder) -> root.get("deletedAt").isNull();
    }

    public static Specification<Product> productCategory(Long productCategoryId) {
        return (root, query, criteriaBuilder) -> {
            if (productCategoryId != null) {
                return criteriaBuilder.equal(root.get("productCategory").get("id"), productCategoryId);
            } else {
                return criteriaBuilder.conjunction();
            }
        };
    }

    public static Specification<Product> searchQuery(String searchQuery) {
        return (root, query, criteriaBuilder) -> {
            if (searchQuery != null && !searchQuery.isEmpty()) {
                return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + searchQuery.toLowerCase() + "%");
            } else {
                return criteriaBuilder.conjunction();
            }
        };

    }
}