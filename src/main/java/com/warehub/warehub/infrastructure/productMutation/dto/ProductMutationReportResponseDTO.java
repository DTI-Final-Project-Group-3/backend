package com.warehub.warehub.infrastructure.productMutation.dto;

import com.warehub.warehub.infrastructure.product.dto.ProductBasicResponseDTO;
import com.warehub.warehub.infrastructure.product.dto.ProductCategoryResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductMutationReportResponseDTO {

    private Long id;

    private OffsetDateTime createdAt;

    private Integer quantity;

    private ProductBasicResponseDTO product;

    private ProductCategoryResponseDTO productCategory;

    private ProductMutationTypeResponseDTO productMutationType;

    private ProductMutationStatusResponseDTO productMutationStatus;

    public ProductMutationReportResponseDTO(Long id, Instant createdAt, Integer quantity,
                                            Long productId, String productName,
                                            Long productCategoryId, String productCategoryName,
                                            Long productMutationTypeId, String productMutationTypeName,
                                            Long productMutationStatusId, String productMutationStatusName){
        this.id = id;
        this.createdAt = OffsetDateTime.ofInstant(createdAt, ZoneOffset.UTC);
        this.quantity = quantity;
        this.product = new ProductBasicResponseDTO(productId, productName);
        this.productCategory = new ProductCategoryResponseDTO(productCategoryId, productCategoryName);
        this.productMutationType = new ProductMutationTypeResponseDTO(productMutationTypeId, productMutationTypeName);
        this.productMutationStatus = new ProductMutationStatusResponseDTO(productMutationStatusId, productMutationStatusName);
    }
}
