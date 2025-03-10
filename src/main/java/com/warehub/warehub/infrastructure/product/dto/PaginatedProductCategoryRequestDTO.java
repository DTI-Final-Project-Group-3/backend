package com.warehub.warehub.infrastructure.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedProductCategoryRequestDTO {
    private int page;
    private int limit;
}
