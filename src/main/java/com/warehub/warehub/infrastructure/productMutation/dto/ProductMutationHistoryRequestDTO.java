package com.warehub.warehub.infrastructure.productMutation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductMutationHistoryRequestDTO {

    private Integer page;
    private Integer limit;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long productId;
    private Long productCategoryId;
    private Long productMutationTypeId;
    private Long productMutationStatusId;
    private Long destinationWarehouseId;

    public ProductMutationHistoryRequestDTO(LocalDate startDate, LocalDate endDate,
                                            Long productId, Long productCategoryId,
                                            Long productMutationTypeId, Long productMutationStatusId,
                                            Long destinationWarehouseId){
        this.startDate = startDate;
        this.endDate = endDate;
        this.productId = productId;
        this.productCategoryId = productCategoryId;
        this.productMutationTypeId = productMutationTypeId;
        this.productMutationStatusId = productMutationStatusId;
        this.destinationWarehouseId = destinationWarehouseId;
    }
}
