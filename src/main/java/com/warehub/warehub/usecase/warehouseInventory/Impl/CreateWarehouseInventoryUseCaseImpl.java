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
import com.warehub.warehub.usecase.warehouseInventory.CreateWarehouseInventoryUseCase;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateWarehouseInventoryUseCaseImpl implements CreateWarehouseInventoryUseCase {

    private final WarehouseInventoryRepository warehouseInventoryRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final UsersRepository usersRepository;
    private final ProductMutationRepository productMutationRepository;
    private final ProductMutationTypeRepository productMutationTypeRepository;
    private final ProductMutationStatusRepository productMutationStatusRepository;

    public CreateWarehouseInventoryUseCaseImpl(WarehouseInventoryRepository warehouseInventoryRepository, ProductRepository productRepository, WarehouseRepository warehouseRepository, UsersRepository usersRepository, ProductMutationRepository productMutationRepository, ProductMutationTypeRepository productMutationTypeRepository, ProductMutationStatusRepository productMutationStatusRepository) {
        this.warehouseInventoryRepository = warehouseInventoryRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
        this.usersRepository = usersRepository;
        this.productMutationRepository = productMutationRepository;
        this.productMutationTypeRepository = productMutationTypeRepository;
        this.productMutationStatusRepository = productMutationStatusRepository;
    }

    @Override
    @Transactional
    public WarehouseInventoryResponseDTO createWarehouseInventory(ProductMutationRequestDTO req) {
        boolean warehouseInventoryExist = warehouseInventoryRepository.existsByProductIdAndWarehouseIdAndDeletedAtIsNull(req.getProductId(), req.getDestinationWarehouseId());
        if (warehouseInventoryExist){
            throw new DuplicateWarehouseInventoryException("Warehouse Inventory with product ID " + req.getProductId() + " and warehouse ID " + req.getDestinationWarehouseId() + " already exist !");
        }
        Product product = productRepository.findByIdAndDeletedAtIsNull(req.getProductId())
                .orElseThrow(()-> new ProductNotFoundException("Product with ID "+ req.getProductId() + " not found !"));

        Warehouse warehouse = warehouseRepository.findByIdAndDeletedAtIsNull(req.getDestinationWarehouseId())
                .orElseThrow(()-> new WarehouseNotFoundException("Warehouse with ID "+ req.getDestinationWarehouseId() + " not found !"));

        // create new inventory
        WarehouseInventory warehouseInventory = new WarehouseInventory();
        warehouseInventory.setWarehouse(warehouse);
        warehouseInventory.setProduct(product);
        warehouseInventory.setQuantity(req.getQuantity());
        warehouseInventoryRepository.save(warehouseInventory);

        // create new mutation record
        User requester = usersRepository.findByIdAndDeletedAtIsNull(req.getRequesterId())
                .orElseThrow(()-> new UsernameNotFoundException("User with ID " + req.getRequesterId() + " not found !"));

        ProductMutationType productMutationType = productMutationTypeRepository.findByIdAndDeletedAtIsNull(MutationConstant.TYPE_CREATE_INVENTORY.getValue())
                .orElseThrow(()-> new ProductMutationTypeNotFoundException("Product mutation type with ID not found !"));

        ProductMutationStatus productMutationStatus = productMutationStatusRepository.findByIdAndDeletedAtIsNull(MutationConstant.STATUS_COMPLETED.getValue())
                .orElseThrow(()-> new ProductMutationStatusNotFoundException("Product mutation status with ID not found !"));

        ProductMutation productMutation = new ProductMutation();
        productMutation.setProduct(product);
        productMutation.setQuantity(req.getQuantity());
        productMutation.setRequester(requester);
        productMutation.setRequesterNotes(req.getRequesterNotes());
        productMutation.setDestinationWarehouse(warehouse);
        productMutation.setProductMutationType(productMutationType);
        productMutation.setProductMutationStatus(productMutationStatus);
        productMutationRepository.save(productMutation);

        return new WarehouseInventoryResponseDTO(warehouseInventory);
    }
}