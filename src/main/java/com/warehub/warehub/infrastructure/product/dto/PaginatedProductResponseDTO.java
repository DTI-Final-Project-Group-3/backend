package com.warehub.warehub.infrastructure.product.dto;

import com.warehub.warehub.entity.Product;
import com.warehub.warehub.entity.WarehouseInventory;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class PaginatedProductResponseDTO {
    private Long productId;
    private String productName;
    private BigDecimal price;
    private String imageUrl;
    private Long productCategoryId;
    private String productCategoryName;

    public PaginatedProductResponseDTO(Product product, String imageUrl){
        this.productId = product.getId();
        this.productName = product.getName();
        this.price = product.getPrice();;
        this.imageUrl = imageUrl;
        this.productCategoryId = product.getProductCategory().getId();
        this.productCategoryName = product.getProductCategory().getName();
    }
}
