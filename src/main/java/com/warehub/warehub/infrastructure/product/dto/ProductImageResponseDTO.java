package com.warehub.warehub.infrastructure.product.dto;

import com.warehub.warehub.entity.ProductImage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductImageResponseDTO {
    private String url;
    private Integer position;

    public ProductImageResponseDTO(ProductImage productImage){
        this.url = productImage.getUrl();
        this.position = productImage.getPosition();
    }
}
