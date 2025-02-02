package com.warehub.warehub.infrastructure.product.dto;

import com.warehub.warehub.entity.ProductCategory;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductCategoryRequestDTO {
    @NotNull
    private String name;

    public ProductCategory toEntity(){
        ProductCategory productCategory = new ProductCategory();
        productCategory.setName(this.name);
        return productCategory;
    }
}
