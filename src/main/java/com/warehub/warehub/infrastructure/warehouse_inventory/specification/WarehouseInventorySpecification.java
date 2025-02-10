package com.warehub.warehub.infrastructure.warehouse_inventory.specification;

import com.warehub.warehub.entity.WarehouseInventory;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class WarehouseInventorySpecification {

    public static Specification<WarehouseInventory> warehouseIn(List<Long> warehouseIds) {
        return (root, query, criteriaBuilder) -> root.get("warehouse").get("id").in(warehouseIds);
    }

    public static Specification<WarehouseInventory> notDeleted() {
        return (root, query, criteriaBuilder) -> root.get("deletedAt").isNull();
    }

    public static Specification<WarehouseInventory> distinct() {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            return null;
        };
    }

    public static Specification<WarehouseInventory> productCategory(Long productCategoryId) {
        return (root, query, criteriaBuilder) -> {
            if (productCategoryId != null) {
                return criteriaBuilder.equal(root.get("product").get("productCategoryId"), productCategoryId);
            } else {
                return criteriaBuilder.conjunction();
            }
        };
    }
}
