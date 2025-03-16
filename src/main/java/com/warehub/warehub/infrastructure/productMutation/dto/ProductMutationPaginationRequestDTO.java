package com.warehub.warehub.infrastructure.productMutation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductMutationPaginationRequestDTO {
    @NotNull
    private int page;

    @NotNull
    private int limit;

    private LocalDate startDate;

    private LocalDate endDate;

    private Long productId;

    private Long productCategoryId;

    private Long originWarehouseId;

    private Long destinationWarehouseId;

    @NotNull
    private List<Long> productMutationTypeId;

    private Long productMutationStatusId;

}
