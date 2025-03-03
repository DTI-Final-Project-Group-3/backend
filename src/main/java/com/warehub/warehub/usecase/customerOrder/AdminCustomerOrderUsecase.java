package com.warehub.warehub.usecase.customerOrder;

import com.warehub.warehub.infrastructure.customerOrders.dto.CustomerOrderResponseDTO;
import com.warehub.warehub.infrastructure.customerOrders.dto.UpdateOrderRequestDTO;

public interface AdminCustomerOrderUsecase {

    CustomerOrderResponseDTO updateCustomerOrder(UpdateOrderRequestDTO requestDTO);
    void autoUpdateCustomerOrderStatus();
}
