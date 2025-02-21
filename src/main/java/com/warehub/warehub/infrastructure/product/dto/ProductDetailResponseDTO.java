package com.warehub.warehub.infrastructure.product.dto;

import com.warehub.warehub.entity.Product;
import com.warehub.warehub.infrastructure.warehouse.dto.WarehouseResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class ProductDetailResponseDTO {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal weight;
    private BigDecimal height;
    private BigDecimal width;
    private BigDecimal length;

    private List<ProductImageResponseDTO> images;

    private ProductCategoryResponseDTO category;

    private Integer totalStock;
    private WarehouseResponseDTO nearestWarehouse;

    public ProductDetailResponseDTO(Product product, List<ProductImageResponseDTO> productImages){
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.weight = product.getWeight();
        this.height = product.getHeight();
        this.width = product.getWidth();
        this.length = product.getLength();

        this.images = productImages;

        this.category = new ProductCategoryResponseDTO(product.getProductCategory());
    }

}

