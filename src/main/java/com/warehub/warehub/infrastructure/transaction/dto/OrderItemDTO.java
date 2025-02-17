package com.warehub.warehub.infrastructure.transaction.dto;

import com.warehub.warehub.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO {
    private Long productId;
    private Integer quantity;
    private BigDecimal unitPrice;
}
