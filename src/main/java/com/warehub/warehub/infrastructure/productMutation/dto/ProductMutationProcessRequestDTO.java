package com.warehub.warehub.infrastructure.productMutation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductMutationProcessRequestDTO {
    @NotNull
    private Long reviewerId;

    private String reviewerNotes;
}
