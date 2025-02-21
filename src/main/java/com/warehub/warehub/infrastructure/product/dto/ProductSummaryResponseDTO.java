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
    private String categoryName;
    private String thumbnail;
    private Long totalStock;

    public ProductSummaryResponseDTO(Long productId, String productName, BigDecimal price, String thumbnail){
        this.id = productId;
        this.name = productName;
        this.price = price;
        this.thumbnail = thumbnail;
    }

    public ProductSummaryResponseDTO(Long productId, String productName, BigDecimal price, String categoryName, String thumbnail, Long totalStock){
        this.id = productId;
        this.name = productName;
        this.price = price;
        this.categoryName = categoryName;
        this.thumbnail = thumbnail;
        this.totalStock = totalStock;
    }
}
