package com.warehub.warehub.infrastructure.product_mutation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class ProductMutationRequestDTO {

    @NotNull
    private Long productId;

    @NotNull
    private Integer quantity;

    private String notes;

    @NotNull
    private Long requesterId;

    private Long approverId;

    private Long originWarehouseId;

    @NotNull
    private Long destinationWarehouseId;

    private Long productMutationTypeId;

    private Long productMutationStatusId;

    private OffsetDateTime acceptedAt;
}
