package com.warehub.warehub.infrastructure.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSummaryResponseDTO {

    private Long id;
    private String name;
    private BigDecimal price;
    private BigDecimal weight;
    private BigDecimal height;
    private BigDecimal width;
    private BigDecimal length;
    private String categoryName;
    private String thumbnail;
    private Long totalStock;

    // use in query
    public ProductSummaryResponseDTO(Long id, String name, BigDecimal price,
                                     BigDecimal weight, BigDecimal height, BigDecimal width, BigDecimal length,
                                     String categoryName, String thumbnail){
        this.id = id;
        this.name = name;
        this.price = price;
        this.weight = weight;
        this.height = height;
        this.width = width;
        this.length = length;
        this.categoryName = categoryName;
        this.thumbnail = thumbnail;
    }

}
