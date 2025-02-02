package com.warehub.warehub.infrastructure.product.dto;

import com.warehub.warehub.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class ProductResponseDTO {

    private Long id;
    private String name;
    private Long productCategoryId;
    private String productCategoryName;
    private String descriptions;
    private BigDecimal price;
    private BigDecimal weight;
    private BigDecimal height;
    private BigDecimal width;
    private BigDecimal length;
    private List<ProductImageResponseDTO> productImages;

    public ProductResponseDTO(Product product){
        this.id = product.getId();
        this.name = product.getName();
        this.productCategoryId = product.getProductCategory().getId();
        this.productCategoryName = product.getProductCategory().getName();
        this.descriptions = product.getDescriptions();
        this.price = product.getPrice();
        this.weight = product.getWeight();
        this.height = product.getHeight();
        this.width = product.getWidth();
        this.length = product.getLength();
    }

    public ProductResponseDTO(Product product, List<ProductImageResponseDTO> productImages){
        this.id = product.getId();
        this.name = product.getName();
        this.productCategoryId = product.getProductCategory().getId();
        this.productCategoryName = product.getProductCategory().getName();
        this.descriptions = product.getDescriptions();
        this.price = product.getPrice();
        this.weight = product.getWeight();
        this.height = product.getHeight();
        this.width = product.getWidth();
        this.length = product.getLength();
        this.productImages = productImages;
    }
}

