package com.warehub.warehub.usecase.user;

import com.warehub.warehub.infrastructure.users.dto.ShippingCostRequestDTO;
import com.warehub.warehub.infrastructure.users.dto.ShippingCostResponseDTO;

public interface ShippingUsecase {
    ShippingCostResponseDTO getCost(ShippingCostRequestDTO requestDTO);
    ShippingCostResponseDTO getCostDummy(ShippingCostRequestDTO requestDTO);
}
