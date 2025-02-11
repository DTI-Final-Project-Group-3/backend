package com.warehub.warehub.infrastructure.product.dto;

import com.warehub.warehub.entity.Product;
import com.warehub.warehub.entity.ProductImage;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductImageRequestDTO {

    @NotNull
    private String url;

    @NotNull
    private Integer position;

    public ProductImage toEntity(Product product){
        ProductImage productImage = new ProductImage();
        productImage.setProduct(product);
        productImage.setUrl(this.url);
        productImage.setPosition(this.position);
        return productImage;
    }
}
