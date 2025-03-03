package com.warehub.warehub.infrastructure.productMutation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductMutationTotalResponseDTO {
    private Integer started;
    private Integer added;
    private Integer reduced;
    private Integer netChange;
    private Integer ending;
}
