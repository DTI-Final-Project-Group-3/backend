package com.warehub.warehub.infrastructure.productMutation.dto;

import com.warehub.warehub.entity.ProductMutation;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class ProductMutationResponseDTO {
    private Long productMutationId;
    private Long productId;
    private Integer quantity;
    private String notes;
    private Long requesterId;
    private Long approverId;
    private Long originWarehouseId;
    private Long destinationWarehouseId;
    private Long productMutationTypeId;
    private String productMutationTypeName;
    private Long productMutationStatusId;
    private OffsetDateTime acceptedAt;

    public ProductMutationResponseDTO(ProductMutation productMutation){
        this.productMutationId = productMutation.getId();
        this.productId = productMutation.getProduct().getId();
        this.quantity = productMutation.getQuantity();
        this.notes = productMutation.getNotes() == null ? null : productMutation.getNotes();
        this.requesterId = productMutation.getRequester().getId();
        this.approverId = productMutation.getApprover() == null ? null : productMutation.getApprover().getId();
        this.originWarehouseId = productMutation.getOriginWarehouse() == null ? null :productMutation.getOriginWarehouse().getId() ;
        this.destinationWarehouseId = productMutation.getDestinationWarehouse().getId();
        this.productMutationTypeId = productMutation.getProductMutationType().getId();
        this.productMutationTypeName = productMutation.getProductMutationType().getName();
        this.productMutationStatusId = productMutation.getProductMutationStatus().getId();
        this.acceptedAt = productMutation.getAcceptedAt();
    }
}
