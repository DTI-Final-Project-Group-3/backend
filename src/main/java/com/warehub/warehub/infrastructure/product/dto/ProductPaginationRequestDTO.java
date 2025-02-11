package com.warehub.warehub.infrastructure.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductPaginationRequestDTO {
    private int page;
    private int limit;
    private Long productCategoryId;
    private String searchQuery;
}
