package com.warehub.warehub.usecase.product_mutation.impl;

import com.warehub.warehub.common.exceptions.*;
import com.warehub.warehub.entity.*;
import com.warehub.warehub.infrastructure.product.repository.ProductRepository;
import com.warehub.warehub.infrastructure.product_mutation.dto.ApproveProductMutationRequestDTO;
import com.warehub.warehub.infrastructure.product_mutation.dto.ProductMutationRequestDTO;
import com.warehub.warehub.infrastructure.product_mutation.dto.ProductMutationResponseDTO;
import com.warehub.warehub.infrastructure.product_mutation.repository.ProductMutationRepository;
import com.warehub.warehub.infrastructure.product_mutation.repository.ProductMutationStatusRepository;
import com.warehub.warehub.infrastructure.product_mutation.repository.ProductMutationTypeRepository;
import com.warehub.warehub.infrastructure.users.repository.UsersRepository;
import com.warehub.warehub.infrastructure.warehouse.repository.WarehouseRepository;
import com.warehub.warehub.usecase.product_mutation.UpdateProductMutationUseCase;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class UpdateProductMutationUseCaseImpl implements UpdateProductMutationUseCase {

    private final ProductMutationRepository productMutationRepository;
    private final ProductMutationStatusRepository productMutationStatusRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductMutationTypeRepository productMutationTypeRepository;
    private final UsersRepository usersRepository;

    public UpdateProductMutationUseCaseImpl(ProductMutationRepository productMutationRepository, ProductMutationStatusRepository productMutationStatusRepository, ProductRepository productRepository, WarehouseRepository warehouseRepository, ProductMutationTypeRepository productMutationTypeRepository, UsersRepository usersRepository) {
        this.productMutationRepository = productMutationRepository;
        this.productMutationStatusRepository = productMutationStatusRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
        this.productMutationTypeRepository = productMutationTypeRepository;
        this.usersRepository = usersRepository;
    }

    @Override
    public ProductMutationResponseDTO updateProductMutationById(Long productMutationId, ProductMutationRequestDTO req) {
        ProductMutation productMutation = productMutationRepository.findByIdAndDeletedAtIsNull(productMutationId)
                .orElseThrow(()-> new ProductMutationNotFoundException("Product mutation with ID "+ productMutationId + " not found !"));

        Product product = productRepository.findByIdAndDeletedAtIsNull(req.getProductId())
                .orElseThrow(()-> new ProductNotFoundException("Product with ID " + req.getProductId() + " not found !"));

        User requester = usersRepository.findByIdAndDeletedAtIsNull(req.getRequesterId())
                .orElseThrow(()-> new UsernameNotFoundException("User with ID " + req.getRequesterId() + " not found !"));

        Warehouse originWarehouse = warehouseRepository.findByIdAndDeletedAtIsNull(req.getOriginWarehouseId())
                .orElseThrow(()-> new WarehouseNotFoundException("Warehouse with ID "+ req.getOriginWarehouseId() + " not found !"));

        Warehouse destinationWarehouse = warehouseRepository.findByIdAndDeletedAtIsNull(req.getDestinationWarehouseId())
                .orElseThrow(()-> new WarehouseNotFoundException("Warehouse with ID "+ req.getDestinationWarehouseId() + " not found !"));

        ProductMutationType productMutationType = productMutationTypeRepository.findByIdAndDeletedAtIsNull(req.getProductMutationTypeId())
                .orElseThrow(()-> new ProductMutationTypeNotFoundException("Product mutation type with ID not found !"));

        ProductMutationStatus productMutationStatus = productMutationStatusRepository.findByIdAndDeletedAtIsNull(req.getProductMutationStatusId())
                .orElseThrow(()-> new ProductMutationStatusNotFoundException("Product mutation status with ID not found !"));

        productMutation.setProduct(product);
        productMutation.setNotes(req.getNotes());
        productMutation.setRequester(requester);
        productMutation.setOriginWarehouse(originWarehouse);
        productMutation.setDestinationWarehouse(destinationWarehouse);
        productMutation.setProductMutationType(productMutationType);
        productMutation.setProductMutationStatus(productMutationStatus);
        productMutation.setAcceptedAt(req.getAcceptedAt());

        return new ProductMutationResponseDTO(productMutation);
    }

    @Override
    public ProductMutationResponseDTO approveManualProductMutation(Long productMutationId, ApproveProductMutationRequestDTO req) {
        ProductMutation productMutation = productMutationRepository.findByIdAndDeletedAtIsNull(productMutationId)
                .orElseThrow(()-> new ProductMutationNotFoundException("Product mutation with ID "+ productMutationId + " not found !"));

        User approver = usersRepository.findByIdAndDeletedAtIsNull(req.getApproverId())
                .orElseThrow(()-> new UsernameNotFoundException("User with ID " + req.getApproverId() + " not found !"));

        ProductMutationStatus productMutationStatusApproved = productMutationStatusRepository.findByIdAndDeletedAtIsNull(2L)
                .orElseThrow(()-> new ProductMutationStatusNotFoundException("Product mutation status with ID not found !"));

        productMutation.setApprover(approver);
        productMutation.setAcceptedAt(OffsetDateTime.now());
        productMutation.setProductMutationStatus(productMutationStatusApproved);

        return new ProductMutationResponseDTO(productMutationRepository.save(productMutation));
    }
}
