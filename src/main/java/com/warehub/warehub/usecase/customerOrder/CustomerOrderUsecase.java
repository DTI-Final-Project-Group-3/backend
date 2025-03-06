package com.warehub.warehub.usecase.customerOrder;

import com.warehub.warehub.common.utils.PaginationInfo;
import com.warehub.warehub.infrastructure.customerOrders.dto.*;

public interface CustomerOrderUsecase {

    PaginationInfo<CustomerOrderResponseDTO> getAllCustomerOrders(PaginatedCustomerOrderRequestDTO request);
    CustomerOrderResponseDTO getCustomerOrder(CustomerOrderDetailRequestDTO request);
    CustomerOrderResponseDTO confirmCustomerOrder(ConfirmOrderRequestDTO request);
    CustomerOrderResponseDTO cancelCustomerOrder(Long customerOrderId);

    PaginationInfo<CustomerOrderHistoryResponseDTO> getHistoryCustomerOrder(CustomerOrderHistoryRequestDTO req);
}
