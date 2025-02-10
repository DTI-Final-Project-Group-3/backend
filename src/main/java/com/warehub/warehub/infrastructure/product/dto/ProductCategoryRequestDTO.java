package com.warehub.warehub.infrastructure.product.dto;

import com.warehub.warehub.entity.ProductCategory;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCategoryRequestDTO {
    @NotNull
    private String name;

    public ProductCategory toEntity(){
        ProductCategory productCategory = new ProductCategory();
        productCategory.setName(this.name);
        return productCategory;
    }
}
