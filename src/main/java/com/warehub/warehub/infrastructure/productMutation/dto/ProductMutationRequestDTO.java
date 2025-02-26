package com.warehub.warehub.infrastructure.productMutation.dto;

import com.warehub.warehub.entity.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class ProductMutationRequestDTO {

    @NotNull
    private Long productId;

    @NotNull
    private Integer quantity;

    private String notes;

    @NotNull
    private Long requesterId;

    private Long originWarehouseId;

    @NotNull
    private Long destinationWarehouseId;

    public ProductMutation toEntity(Product product, User requester, Warehouse destinationWarehouse, ProductMutationStatus status, ProductMutationType type){
        ProductMutation productMutation = new ProductMutation();

        productMutation.setProduct(product);
        productMutation.setQuantity(this.quantity);
        productMutation.setNotes(this.notes);
        productMutation.setRequester(requester);
        productMutation.setDestinationWarehouse(destinationWarehouse);
        productMutation.setProductMutationStatus(status);
        productMutation.setProductMutationType(type);

        return productMutation;
    }

    public ProductMutation toEntity(Product product, User requester, Warehouse originWarehouse, Warehouse destinationWarehouse, ProductMutationStatus status, ProductMutationType type){
        ProductMutation productMutation = new ProductMutation();

        productMutation.setProduct(product);
        productMutation.setQuantity(this.quantity);
        productMutation.setNotes(this.notes);
        productMutation.setRequester(requester);
        productMutation.setOriginWarehouse(originWarehouse);
        productMutation.setDestinationWarehouse(destinationWarehouse);
        productMutation.setProductMutationStatus(status);
        productMutation.setProductMutationType(type);

        return productMutation;
    }

}
