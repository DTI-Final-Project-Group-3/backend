package com.warehub.warehub.usecase.warehouseInventory.Impl;

import com.warehub.warehub.common.utils.PaginationInfo;
import com.warehub.warehub.common.utils.ValidationService;
import com.warehub.warehub.infrastructure.warehouseInventory.dto.WarehouseInventoryPaginationRequestDTO;
import com.warehub.warehub.infrastructure.warehouseInventory.dto.WarehouseInventoryPaginationResponseDTO;
import com.warehub.warehub.infrastructure.warehouseInventory.repository.WarehouseInventoryRepository;
import com.warehub.warehub.usecase.warehouseInventory.GetWarehouseInventoryUseCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class GetWarehouseInventoryUseCaseImpl implements GetWarehouseInventoryUseCase {

    private final ValidationService validationService;
    private final WarehouseInventoryRepository warehouseInventoryRepository;

    public GetWarehouseInventoryUseCaseImpl(ValidationService validationService, WarehouseInventoryRepository warehouseInventoryRepository) {
        this.validationService = validationService;
        this.warehouseInventoryRepository = warehouseInventoryRepository;
    }


    @Override
    public PaginationInfo<WarehouseInventoryPaginationResponseDTO> getPaginatedWarehouseInventoryByWarehouseId(WarehouseInventoryPaginationRequestDTO req) {

        PageRequest pageRequest = PageRequest.of(req.getPage(), req.getLimit());
        validationService.validateWarehouseId(req.getWarehouseId(), "Warehouse");

        Page<WarehouseInventoryPaginationResponseDTO> inventories = warehouseInventoryRepository.findByWarehouseId(req.getWarehouseId(), pageRequest);

        return new PaginationInfo<>(inventories, inventories.getContent());
    }
}