package com.warehub.warehub.infrastructure.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedProductRequestDTO {
    private int page;
    private int limit;
    private double longitude;
    private double latitude;
    private Long productCategoryId;
    private String searchQuery;
}
