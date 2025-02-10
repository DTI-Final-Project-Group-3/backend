package com.warehub.warehub.infrastructure.product_mutation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApproveProductMutationRequestDTO {
    @NotNull
    private Long approverId;
}
