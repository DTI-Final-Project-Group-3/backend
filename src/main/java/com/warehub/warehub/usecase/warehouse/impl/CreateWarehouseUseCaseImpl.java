package com.warehub.warehub.usecase.warehouse.impl;

import com.warehub.warehub.common.exceptions.DuplicateWarehouseException;
import com.warehub.warehub.entity.Warehouse;
import com.warehub.warehub.infrastructure.warehouse.dto.WarehouseRequestDTO;
import com.warehub.warehub.infrastructure.warehouse.dto.WarehouseResponseDTO;
import com.warehub.warehub.infrastructure.warehouse.repository.WarehouseRepository;
import com.warehub.warehub.usecase.warehouse.CreateWarehouseUseCase;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CreateWarehouseUseCaseImpl implements CreateWarehouseUseCase {

    private final WarehouseRepository warehouseRepository;

    public CreateWarehouseUseCaseImpl(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    @Override
    public WarehouseResponseDTO createWarehouse(WarehouseRequestDTO req) {
        Optional<Warehouse> warehouse = warehouseRepository.findByNameIgnoreCase(req.getName());

        if (warehouse.isPresent()){
            throw new DuplicateWarehouseException("Warehouse with name " + req.getName() + " already exist !");
        }

        Warehouse newWarehouse = warehouseRepository.save(req.toEntity());

        return new WarehouseResponseDTO(newWarehouse);
    }
}
