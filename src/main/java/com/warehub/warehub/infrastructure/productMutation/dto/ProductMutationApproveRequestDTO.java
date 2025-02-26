package com.warehub.warehub.infrastructure.productMutation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductMutationApproveRequestDTO {
    @NotNull
    private Long approverId;
}
