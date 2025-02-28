package com.warehub.warehub.usecase.warehouseInventory.Impl;

import com.warehub.warehub.common.enums.MutationConstant;
import com.warehub.warehub.common.exceptions.*;
import com.warehub.warehub.entity.*;
import com.warehub.warehub.infrastructure.product.repository.ProductRepository;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationRequestDTO;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationRepository;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationStatusRepository;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationTypeRepository;
import com.warehub.warehub.infrastructure.users.repository.UsersRepository;
import com.warehub.warehub.infrastructure.warehouse.repository.WarehouseRepository;
import com.warehub.warehub.infrastructure.warehouseInventory.dto.WarehouseInventoryResponseDTO;
import com.warehub.warehub.infrastructure.warehouseInventory.repository.WarehouseInventoryRepository;
import com.warehub.warehub.usecase.warehouseInventory.UpdateWarehouseInventoryUseCase;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateWarehouseInventoryUseCaseImpl implements UpdateWarehouseInventoryUseCase {

    private final WarehouseInventoryRepository warehouseInventoryRepository;
    private final ProductMutationRepository productMutationRepository;
    private final ProductRepository productRepository;
    private final UsersRepository usersRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductMutationStatusRepository productMutationStatusRepository;
    private final ProductMutationTypeRepository productMutationTypeRepository;

    public UpdateWarehouseInventoryUseCaseImpl(WarehouseInventoryRepository warehouseInventoryRepository, ProductMutationRepository productMutationRepository, ProductRepository productRepository, UsersRepository usersRepository, WarehouseRepository warehouseRepository, ProductMutationStatusRepository productMutationStatusRepository, ProductMutationTypeRepository productMutationTypeRepository) {
        this.warehouseInventoryRepository = warehouseInventoryRepository;
        this.productMutationRepository = productMutationRepository;
        this.productRepository = productRepository;
        this.usersRepository = usersRepository;
        this.warehouseRepository = warehouseRepository;
        this.productMutationStatusRepository = productMutationStatusRepository;
        this.productMutationTypeRepository = productMutationTypeRepository;
    }

    @Override
    @Transactional
    public WarehouseInventoryResponseDTO updateQuantity(Long warehouseInventoryId, ProductMutationRequestDTO req) {
        WarehouseInventory warehouseInventory = warehouseInventoryRepository.findByIdAndDeletedAtIsNull(warehouseInventoryId)
                .orElseThrow(()-> new WarehouseInventoryNotFoundException("Warehouse inventory with ID "+ warehouseInventoryId + " not found !"));

        // update quantity on inventory
        Integer updateQuantity = warehouseInventory.getQuantity() + req.getQuantity();
        if (updateQuantity < 0){
            throw new NegativeQuantityException("Quantity cannot be negative: " + updateQuantity);
        }
        warehouseInventory.setQuantity(updateQuantity);

        // create journal on product mutation
        Product product = productRepository.findByIdAndDeletedAtIsNull(req.getProductId())
                .orElseThrow(()-> new ProductNotFoundException("Product with ID " + req.getProductId() + " not found !"));

        User requester = usersRepository.findByIdAndDeletedAtIsNull(req.getRequesterId())
                .orElseThrow(()-> new UsernameNotFoundException("User with ID " + req.getRequesterId() + " not found !"));

        Warehouse destinationWarehouse = warehouseRepository.findByIdAndDeletedAtIsNull(req.getDestinationWarehouseId())
                .orElseThrow(()-> new WarehouseNotFoundException("Warehouse with ID "+ req.getDestinationWarehouseId() + " not found !"));

        ProductMutationType productMutationType = productMutationTypeRepository.findByIdAndDeletedAtIsNull(MutationConstant.TYPE_UPDATE_INVENTORY.getValue())
                .orElseThrow(()-> new ProductMutationTypeNotFoundException("Product mutation type with ID not found !"));

        ProductMutationStatus productMutationStatus = productMutationStatusRepository.findByIdAndDeletedAtIsNull(MutationConstant.STATUS_COMPLETED.getValue())
                .orElseThrow(()-> new ProductMutationStatusNotFoundException("Product mutation status with ID not found !"));

        productMutationRepository.save(req.toEntity(product, requester, destinationWarehouse, productMutationStatus, productMutationType));

        return new WarehouseInventoryResponseDTO(warehouseInventoryRepository.save(warehouseInventory));
    }
}
