package com.warehub.warehub.infrastructure.product.dto;

import com.warehub.warehub.entity.Product;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductResponseDTO {
    private Long id;
    private String name;
    private BigDecimal price;

    public ProductResponseDTO (Product product){
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
    }
}
