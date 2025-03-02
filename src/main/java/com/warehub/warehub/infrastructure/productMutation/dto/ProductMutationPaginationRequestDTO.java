package com.warehub.warehub.infrastructure.productMutation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductMutationPaginationRequestDTO {
    @NotNull
    private int page;

    @NotNull
    private int limit;

    private Long originWarehouseId;

    private Long destinationWarehouseId;

    private List<Long> productMutationTypeId;

}