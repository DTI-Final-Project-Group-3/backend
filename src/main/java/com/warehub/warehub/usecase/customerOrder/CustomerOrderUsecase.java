package com.warehub.warehub.usecase.customerOrder;

import com.warehub.warehub.common.utils.PaginationInfo;
import com.warehub.warehub.infrastructure.customerOrders.dto.ConfirmOrderRequestDTO;
import com.warehub.warehub.infrastructure.customerOrders.dto.CustomerOrderResponseDTO;
import com.warehub.warehub.infrastructure.customerOrders.dto.CustomerOrderDetailRequestDTO;
import com.warehub.warehub.infrastructure.customerOrders.dto.PaginatedCustomerOrderRequestDTO;

public interface CustomerOrderUsecase {

    PaginationInfo<CustomerOrderResponseDTO> getAllCustomerOrders(PaginatedCustomerOrderRequestDTO request);
    CustomerOrderResponseDTO getCustomerOrder(CustomerOrderDetailRequestDTO request);
    CustomerOrderResponseDTO confirmCustomerOrder(ConfirmOrderRequestDTO request);
    CustomerOrderResponseDTO cancelCustomerOrder(Long customerOrderId);
}
