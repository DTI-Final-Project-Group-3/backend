package com.warehub.warehub.infrastructure.productMutation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductMutationDetailResponseDTO {
    private Long productMutationId;
    private Long productId;
    private String productName;
    private String productThumbnail;
    private Integer quantity;
    private String notes;
    private Long requesterId;
    private String requesterName;
    private Long approverId;
    private String approverName;
    private Long originWarehouseId;
    private String originWarehouseName;
    private Long destinationWarehouseId;
    private String destinationWarehouseName;
    private Long productMutationTypeId;
    private String productMutationTypeName;
    private Long productMutationStatusId;
    private String productMutationStatusName;
    private Instant createdAt;
    private Instant acceptedAt;
}
