package com.warehub.warehub.usecase.user;

import com.warehub.warehub.infrastructure.users.dto.UserAddressRequestDTO;
import com.warehub.warehub.infrastructure.users.dto.UserAddressResponseDTO;

import java.util.List;

public interface UserAddressUsecase {
    List<UserAddressResponseDTO> getAll();
    UserAddressResponseDTO getMain();
    UserAddressResponseDTO read(Long id);
    UserAddressResponseDTO create(UserAddressRequestDTO request);
    UserAddressResponseDTO update(Long id,UserAddressRequestDTO request);
    UserAddressResponseDTO delete(Long id);
}
