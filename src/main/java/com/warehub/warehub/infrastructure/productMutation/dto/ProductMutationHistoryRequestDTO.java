package com.warehub.warehub.infrastructure.productMutation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductMutationHistoryRequestDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private Long productId;
    private Long productCategoryId;
    private Long productMutationTypeId;
    private Long productMutationStatusId;
    private Long warehouseId;
}
