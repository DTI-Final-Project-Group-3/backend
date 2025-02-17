package com.warehub.warehub.infrastructure.customerOrders.dto;

import com.warehub.warehub.entity.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerOrderRequestDTO {

    private Long id;
    private User user;
    private List<CustomerOrderItem> customerOrderitems;
    private Warehouse warehouse;
    private PaymentMethod paymentMethod;
    private List<CustomerOrderItem> orderItems;
    private String paymentProofImageUrl;
    private String gatewayTrxId;
    private BigDecimal shippingCost;
    private BigDecimal totalAmount;
    private CustomerOrderStatus orderStatus;
    private String invoiceCode;

}
