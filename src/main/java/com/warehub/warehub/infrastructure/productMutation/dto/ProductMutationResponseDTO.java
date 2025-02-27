package com.warehub.warehub.infrastructure.productMutation.dto;

import com.warehub.warehub.entity.ProductMutation;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class ProductMutationResponseDTO {
    private Long productMutationId;
    private Long productId;
    private Integer quantity;
    private Long requesterId;
    private String requesterNotes;
    private Long reviewerId;
    private String reviewerNotes;
    private Long originWarehouseId;
    private Long destinationWarehouseId;
    private Long productMutationTypeId;
    private String productMutationTypeName;
    private Long productMutationStatusId;
    private OffsetDateTime reviewedAt;
    private String invoiceCode;

    public ProductMutationResponseDTO(ProductMutation productMutation){
        this.productMutationId = productMutation.getId();
        this.productId = productMutation.getProduct().getId();
        this.quantity = productMutation.getQuantity();
        this.requesterId = productMutation.getRequester().getId();
        this.requesterNotes = productMutation.getRequesterNotes() == null ? null : productMutation.getRequesterNotes();
        this.reviewerId = productMutation.getReviewer() == null ? null : productMutation.getReviewer().getId();
        this.reviewerNotes = productMutation.getReviewerNotes() == null ? null : productMutation.getReviewerNotes();
        this.originWarehouseId = productMutation.getOriginWarehouse() == null ? null :productMutation.getOriginWarehouse().getId() ;
        this.destinationWarehouseId = productMutation.getDestinationWarehouse().getId();
        this.productMutationTypeId = productMutation.getProductMutationType().getId();
        this.productMutationTypeName = productMutation.getProductMutationType().getName();
        this.productMutationStatusId = productMutation.getProductMutationStatus().getId();
        this.reviewedAt = productMutation.getReviewedAt();
        this.invoiceCode = productMutation.getInvoiceCode();
    }
}
