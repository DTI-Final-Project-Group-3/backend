package com.warehub.warehub.infrastructure.product.dto;

import com.warehub.warehub.entity.Product;
import com.warehub.warehub.entity.ProductCategory;
import com.warehub.warehub.entity.ProductImage;
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

    private String descriptions;

    @NotNull
    private BigDecimal price;

    @NotNull
    private BigDecimal weight;

    private BigDecimal height;

    private BigDecimal width;

    private BigDecimal length;

    @Max(5)
    private List<ProductImageRequestDTO> productImages;

    public Product toEntity(ProductCategory productCategory){
        Product product = new Product();
        product.setName(this.name);
        product.setProductCategory(productCategory);
        product.setDescriptions(this.descriptions);
        product.setPrice(this.price);
        product.setWeight(this.weight);
        product.setHeight(this.height);
        product.setWidth(this.width);
        product.setLength(this.length);
        return product;
    }

}
