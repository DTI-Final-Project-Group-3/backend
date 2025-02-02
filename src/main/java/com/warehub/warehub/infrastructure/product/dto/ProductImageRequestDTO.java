package com.warehub.warehub.infrastructure.product.dto;

import com.warehub.warehub.entity.Product;
import com.warehub.warehub.entity.ProductImage;
import lombok.Data;

@Data
public class ProductImageRequestDTO {
    private Long productId;
    private String imageUrl;
    private Integer orderNumber;

    public ProductImage toEntity(Product product){
        ProductImage productImage = new ProductImage();
        productImage.setProduct(product);
        productImage.setImageUrl(this.imageUrl);
        productImage.setOrderNumber(this.orderNumber);
        return productImage;
    }
}
