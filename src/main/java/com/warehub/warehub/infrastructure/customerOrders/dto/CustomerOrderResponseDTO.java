package com.warehub.warehub.infrastructure.customerOrders.dto;

import com.warehub.warehub.entity.CustomerOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerOrderResponseDTO {

    private Long id;
    private Long userId;
    private Long warehouseId;
    private String warehouseName;
    private Long paymentMethodId;
    private String paymentMethodName;
    private List<CustomerOrderItemsDTO> customerOrderitems;
    private String paymentProofImageUrl;
    private String gatewayTrxId;
    private BigDecimal shippingCost;
    private BigDecimal totalAmount;
    private Long orderStatusId;
    private String orderStatusName;
    private String invoiceCode;
    private OffsetDateTime createdAt;

    public static CustomerOrderResponseDTO mapToDTO(CustomerOrder order) {
        return new CustomerOrderResponseDTO(
                order.getId(),
                order.getUser().getId(),
                order.getWarehouse().getId(),
                order.getWarehouse().getName(),
                order.getPaymentMethod().getId(),
                order.getPaymentMethod().getName(),
                order.getCustomerOrderitems().stream()
                        .map(item -> new CustomerOrderItemsDTO(
                                item.getId(),
                                order.getId(),
                                item.getProduct().getId(),
                                item.getProduct().getName(),
                                item.getQuantity(),
                                item.getProductPrice()
                        )).toList(),
                order.getPaymentProofImageUrl(),
                order.getGatewayTrxId(),
                order.getShippingCost(),
                order.getTotalAmount(),
                order.getOrderStatus().getId(),
                order.getOrderStatus().getName(),
                order.getInvoiceCode(),
                order.getCreatedAt()
        );
    }
}
