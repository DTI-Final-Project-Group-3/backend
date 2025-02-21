package com.warehub.warehub.infrastructure.product.dto;

import com.warehub.warehub.entity.Product;
import com.warehub.warehub.entity.ProductCategory;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductRequestDTO {

    @Size(max = 255)
    @NotNull
    private String name;

    @NotNull
    private Long productCategoryId;

    private String description;

    @NotNull
    private BigDecimal price;

    @NotNull
    private BigDecimal weight;

    private BigDecimal height;

    private BigDecimal width;

    private BigDecimal length;

    @Max(5)
    private List<ProductImageRequestDTO> images;

    public Product toEntity(ProductCategory productCategory){
        Product product = new Product();
        product.setName(this.name);
        product.setProductCategory(productCategory);
        product.setDescription(this.description);
        product.setPrice(this.price);
        product.setWeight(this.weight);
        product.setHeight(this.height);
        product.setWidth(this.width);
        product.setLength(this.length);
        return product;
    }

}
