package com.warehub.warehub.infrastructure.productMutation.dto;

import com.warehub.warehub.entity.ProductMutation;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class ProductMutationResponseDTO {
    private Long productMutationId;
    private Long productId;
    private String productName;
    private String productCategoryName;
    private Integer quantity;
    private Long requesterId;
    private String requesterName;
    private String requesterNotes;
    private Long reviewerId;
    private String reviewerName;
    private String reviewerNotes;
    private Long originWarehouseId;
    private String originWarehouseName;
    private Long destinationWarehouseId;
    private String destinationWarehouseName;
    private Long productMutationTypeId;
    private String productMutationTypeName;
    private Long productMutationStatusId;
    private String productMutationStatusName;
    private OffsetDateTime reviewedAt;
    private String invoiceCode;
    private String productMutationCode;

    public ProductMutationResponseDTO(ProductMutation productMutation) {
        this.productMutationId = productMutation.getId();
        this.productId = productMutation.getProduct().getId();
        this.productName = productMutation.getProduct().getName();
        this.productCategoryName = productMutation.getProduct().getProductCategory() != null ?
                                   productMutation.getProduct().getProductCategory().getName() : null;
        this.quantity = productMutation.getQuantity();

        this.requesterId = productMutation.getRequester().getId();
        this.requesterName = productMutation.getRequester().getFullname();
        this.requesterNotes = productMutation.getRequesterNotes();

        if (productMutation.getReviewer() != null) {
            this.reviewerId = productMutation.getReviewer().getId();
            this.reviewerName = productMutation.getReviewer().getFullname();
        }
        this.reviewerNotes = productMutation.getReviewerNotes();

        if (productMutation.getOriginWarehouse() != null) {
            this.originWarehouseId = productMutation.getOriginWarehouse().getId();
            this.originWarehouseName = productMutation.getOriginWarehouse().getName();
        }

        if (productMutation.getDestinationWarehouse() != null) {
            this.destinationWarehouseId = productMutation.getDestinationWarehouse().getId();
            this.destinationWarehouseName = productMutation.getDestinationWarehouse().getName();
        }

        this.productMutationTypeId = productMutation.getProductMutationType().getId();
        this.productMutationTypeName = productMutation.getProductMutationType().getName();
        this.productMutationStatusId = productMutation.getProductMutationStatus().getId();
        this.productMutationStatusName = productMutation.getProductMutationStatus().getName();
        this.reviewedAt = productMutation.getReviewedAt();
        this.invoiceCode = productMutation.getInvoiceCode();
        this.productMutationCode = productMutation.getProductMutationCode();
    }
}
