package com.warehub.warehub.usecase.productMutation.impl;

import com.warehub.warehub.common.exceptions.ProductMutationStatusNotFoundException;
import com.warehub.warehub.common.exceptions.ProductMutationTypeNotFoundException;
import com.warehub.warehub.common.exceptions.ProductNotFoundException;
import com.warehub.warehub.common.exceptions.WarehouseNotFoundException;
import com.warehub.warehub.entity.*;
import com.warehub.warehub.infrastructure.product.repository.ProductRepository;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationRequestDTO;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationResponseDTO;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationRepository;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationStatusRepository;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationTypeRepository;
import com.warehub.warehub.infrastructure.users.repository.UsersRepository;
import com.warehub.warehub.infrastructure.warehouse.repository.WarehouseRepository;
import com.warehub.warehub.usecase.productMutation.CreateProductMutationUseCase;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CreateProductMutationUseCaseImpl implements CreateProductMutationUseCase {

    private final ProductMutationRepository productMutationRepository;
    private final ProductRepository productRepository;
    private final UsersRepository usersRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductMutationTypeRepository productMutationTypeRepository;
    private final ProductMutationStatusRepository productMutationStatusRepository;

    public CreateProductMutationUseCaseImpl(ProductMutationRepository productMutationRepository, ProductRepository productRepository, UsersRepository usersRepository, WarehouseRepository warehouseRepository, ProductMutationTypeRepository productMutationTypeRepository, ProductMutationStatusRepository productMutationStatusRepository) {
        this.productMutationRepository = productMutationRepository;
        this.productRepository = productRepository;
        this.usersRepository = usersRepository;
        this.warehouseRepository = warehouseRepository;
        this.productMutationTypeRepository = productMutationTypeRepository;
        this.productMutationStatusRepository = productMutationStatusRepository;
    }

    @Override
    public ProductMutationResponseDTO createManualMutation(ProductMutationRequestDTO req) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(req.getProductId())
                .orElseThrow(()-> new ProductNotFoundException("Product with ID " + req.getProductId() + " not found !"));

        User requester = usersRepository.findByIdAndDeletedAtIsNull(req.getRequesterId())
                .orElseThrow(()-> new UsernameNotFoundException("User with ID " + req.getRequesterId() + " not found !"));
        
        Warehouse originWarehouse = warehouseRepository.findByIdAndDeletedAtIsNull(req.getOriginWarehouseId())
                .orElseThrow(()-> new WarehouseNotFoundException("Warehouse with ID "+ req.getOriginWarehouseId() + " not found !"));

        Warehouse destinationWarehouse = warehouseRepository.findByIdAndDeletedAtIsNull(req.getDestinationWarehouseId())
                .orElseThrow(()-> new WarehouseNotFoundException("Warehouse with ID "+ req.getDestinationWarehouseId() + " not found !"));        
        
        ProductMutationType productMutationTypeManual = productMutationTypeRepository.findByIdAndDeletedAtIsNull(1L)
                .orElseThrow(()-> new ProductMutationTypeNotFoundException("Product mutation type with ID not found !"));

        ProductMutationStatus productMutationStatusPending = productMutationStatusRepository.findByIdAndDeletedAtIsNull(1L)
                .orElseThrow(()-> new ProductMutationStatusNotFoundException("Product mutation status with ID not found !"));

        ProductMutation productMutation = new ProductMutation();
        productMutation.setProduct(product);
        productMutation.setQuantity(req.getQuantity());
        productMutation.setNotes(req.getNotes());
        productMutation.setRequester(requester);
        productMutation.setOriginWarehouse(originWarehouse);
        productMutation.setDestinationWarehouse(destinationWarehouse);
        productMutation.setProductMutationType(productMutationTypeManual);
        productMutation.setProductMutationStatus(productMutationStatusPending);

        return new ProductMutationResponseDTO(productMutationRepository.save(productMutation));
    }

    @Override
    public ProductMutationResponseDTO createAutoMutation(ProductMutationRequestDTO req) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(req.getProductId())
                .orElseThrow(()-> new ProductNotFoundException("Product with ID " + req.getProductId() + " not found !"));

        User requester = usersRepository.findByIdAndDeletedAtIsNull(req.getRequesterId())
                .orElseThrow(()-> new UsernameNotFoundException("User with ID " + req.getRequesterId() + " not found !"));

        Warehouse destinationWarehouse = warehouseRepository.findByIdAndDeletedAtIsNull(req.getDestinationWarehouseId())
                .orElseThrow(()-> new WarehouseNotFoundException("Warehouse with ID "+ req.getDestinationWarehouseId() + " not found !"));

        ProductMutationType productMutationTypeAuto = productMutationTypeRepository.findByIdAndDeletedAtIsNull(2L)
                .orElseThrow(()-> new ProductMutationTypeNotFoundException("Product mutation type with ID not found !"));

        ProductMutationStatus productMutationStatusReceived = productMutationStatusRepository.findByIdAndDeletedAtIsNull(4L)
                .orElseThrow(()-> new ProductMutationStatusNotFoundException("Product mutation status with ID not found !"));

        ProductMutation productMutation = new ProductMutation();
        productMutation.setProduct(product);
        productMutation.setQuantity(req.getQuantity());
        productMutation.setNotes(req.getNotes());
        productMutation.setRequester(requester);
        productMutation.setDestinationWarehouse(destinationWarehouse);
        productMutation.setProductMutationType(productMutationTypeAuto);
        productMutation.setProductMutationStatus(productMutationStatusReceived);

        return new ProductMutationResponseDTO(productMutationRepository.save(productMutation));
    }
}
