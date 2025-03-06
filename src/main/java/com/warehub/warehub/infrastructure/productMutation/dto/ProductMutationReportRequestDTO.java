package com.warehub.warehub.infrastructure.productMutation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductMutationReportRequestDTO {
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;
    private Long productId;
    private Long productCategoryId;
    private Long productMutationTypeId;
    private Long productMutationStatusId;
}
