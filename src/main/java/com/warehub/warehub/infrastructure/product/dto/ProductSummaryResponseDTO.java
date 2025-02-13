package com.warehub.warehub.infrastructure.product.dto;

import com.warehub.warehub.entity.Product;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class ProductSummaryResponseDTO {

    private Long id;
    private String name;
    private BigDecimal price;

    private String thumbnail;

    private ProductCategoryResponseDTO category;

    public ProductSummaryResponseDTO(Product product, String thumbnail){
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();

        this.thumbnail = thumbnail;

        this.category = new ProductCategoryResponseDTO(product.getProductCategory());
    }

    public ProductSummaryResponseDTO(Long productId, String productName, BigDecimal price,
                                     Long productCategoryId, String productCategoryName,
                                     String thumbnail){
        this.id = productId;
        this.name = productName;
        this.price = price;

        this.category = new ProductCategoryResponseDTO(productCategoryId, productCategoryName);

        this.thumbnail = thumbnail;
    }
}
