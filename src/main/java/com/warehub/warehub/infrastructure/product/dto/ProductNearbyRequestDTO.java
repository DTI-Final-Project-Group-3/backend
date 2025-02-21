package com.warehub.warehub.infrastructure.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductNearbyRequestDTO {
    private Double longitude;
    private Double latitude;
    private Double radius;
    private Long productId;
}

