package com.warehub.warehub.infrastructure.product.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductPaginationRequestDTO {

    @NotNull
    private int page;

    @NotNull
    private int limit;

    private Double longitude;

    private Double latitude;

    private Double radius;

    private Long productCategoryId;

    private String searchQuery;

    public ProductPaginationRequestDTO(int page, int limit, Long productCategoryId, String searchQuery){
        this.page = page;
        this.limit = limit;
        this.productCategoryId = productCategoryId;
        this.searchQuery = searchQuery;
    }
}
