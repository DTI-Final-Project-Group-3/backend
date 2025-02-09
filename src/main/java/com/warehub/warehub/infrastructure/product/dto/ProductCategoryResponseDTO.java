package com.warehub.warehub.infrastructure.product.dto;

import com.warehub.warehub.entity.ProductCategory;
import lombok.Data;

@Data
public class ProductCategoryResponseDTO {
    private Long id;
    private String name;

    public ProductCategoryResponseDTO(ProductCategory productCategory){
        this.id = productCategory.getId();
        this.name = productCategory.getName();
    }
}
