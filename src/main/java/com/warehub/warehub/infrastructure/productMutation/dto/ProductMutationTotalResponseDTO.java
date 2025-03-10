package com.warehub.warehub.infrastructure.productMutation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductMutationTotalResponseDTO {
    private Long started;
    private Long added;
    private Long reduced;
    private Long netChange;
    private Long ending;
}
