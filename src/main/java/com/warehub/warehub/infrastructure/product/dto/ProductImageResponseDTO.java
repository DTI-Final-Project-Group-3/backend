package com.warehub.warehub.infrastructure.product.dto;

import com.warehub.warehub.entity.ProductImage;
import lombok.Data;

@Data
public class ProductImageResponseDTO {
    private String imageUrl;
    private Integer orderNumber;

    public ProductImageResponseDTO(ProductImage productImage){
        this.imageUrl = productImage.getImageUrl();
        this.orderNumber = productImage.getOrderNumber();
    }
}
