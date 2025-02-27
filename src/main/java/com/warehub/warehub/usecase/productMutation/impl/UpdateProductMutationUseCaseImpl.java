package com.warehub.warehub.usecase.productMutation.impl;

import com.warehub.warehub.common.exceptions.*;
import com.warehub.warehub.entity.*;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationProcessRequestDTO;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationResponseDTO;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationRepository;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationStatusRepository;
import com.warehub.warehub.infrastructure.users.repository.UsersRepository;
import com.warehub.warehub.infrastructure.warehouseInventory.repository.WarehouseInventoryRepository;
import com.warehub.warehub.usecase.productMutation.UpdateProductMutationUseCase;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class UpdateProductMutationUseCaseImpl implements UpdateProductMutationUseCase {

    private final ProductMutationRepository productMutationRepository;
    private final ProductMutationStatusRepository productMutationStatusRepository;
    private final UsersRepository usersRepository;
    private final WarehouseInventoryRepository warehouseInventoryRepository;

    public UpdateProductMutationUseCaseImpl(ProductMutationRepository productMutationRepository, ProductMutationStatusRepository productMutationStatusRepository, UsersRepository usersRepository, WarehouseInventoryRepository warehouseInventoryRepository) {
        this.productMutationRepository = productMutationRepository;
        this.productMutationStatusRepository = productMutationStatusRepository;
        this.usersRepository = usersRepository;
        this.warehouseInventoryRepository = warehouseInventoryRepository;
    }

    @Override
    @Transactional
    public ProductMutationResponseDTO approveManualProductMutation(Long productMutationId, ProductMutationProcessRequestDTO req) {

        User reviewer = usersRepository.findByIdAndDeletedAtIsNull(req.getReviewerId())
                .orElseThrow(()-> new UsernameNotFoundException("User with ID " + req.getReviewerId() + " not found !"));

        ProductMutationStatus productMutationStatusApproved = productMutationStatusRepository.findByNameIgnoreCaseAndDeletedAtIsNull("completed")
                .orElseThrow(()-> new ProductMutationStatusNotFoundException("Product mutation status with ID not found !"));

        ProductMutation productMutation = productMutationRepository.findByIdAndDeletedAtIsNull(productMutationId)
                .orElseThrow(()-> new ProductMutationNotFoundException("Product mutation with ID "+ productMutationId + " not found !"));

        WarehouseInventory originWarehouseInventory = warehouseInventoryRepository.findByProductIdAndWarehouseIdAndDeletedAtIsNull(productMutation.getProduct().getId(), productMutation.getOriginWarehouse().getId())
                .orElseThrow(()-> new WarehouseInventoryNotFoundException("Origin warehouse inventory not found "));

        WarehouseInventory destinationWarehouseInventory = warehouseInventoryRepository.findByProductIdAndWarehouseIdAndDeletedAtIsNull(productMutation.getProduct().getId(), productMutation.getDestinationWarehouse().getId())
                .orElseThrow(()-> new WarehouseInventoryNotFoundException("Destination warehouse inventory not found "));

        // decrease quantity from origin warehouse
        originWarehouseInventory.setQuantity(originWarehouseInventory.getQuantity() - productMutation.getQuantity());
        warehouseInventoryRepository.save(destinationWarehouseInventory);

        // increase quantity from destination warehouse
        destinationWarehouseInventory.setQuantity(destinationWarehouseInventory.getQuantity() + productMutation.getQuantity());
        warehouseInventoryRepository.save(destinationWarehouseInventory);

        // update product mutation to completed
        productMutation.setReviewer(reviewer);
        productMutation.setReviewerNotes(req.getReviewerNotes());
        productMutation.setReviewedAt(OffsetDateTime.now());
        productMutation.setProductMutationStatus(productMutationStatusApproved);

        return new ProductMutationResponseDTO(productMutationRepository.save(productMutation));
    }

    @Override
    @Transactional
    public ProductMutationResponseDTO declineManualProductMutation(Long productMutationId, ProductMutationProcessRequestDTO req) {
        User reviewer = usersRepository.findByIdAndDeletedAtIsNull(req.getReviewerId())
                .orElseThrow(()-> new UsernameNotFoundException("User with ID " + req.getReviewerId() + " not found !"));

        ProductMutationStatus productMutationStatusDeclined = productMutationStatusRepository.findByNameIgnoreCaseAndDeletedAtIsNull("declined")
                .orElseThrow(()-> new ProductMutationStatusNotFoundException("Product mutation status with ID not found !"));

        ProductMutation productMutation = productMutationRepository.findByIdAndDeletedAtIsNull(productMutationId)
                .orElseThrow(()-> new ProductMutationNotFoundException("Product mutation with ID "+ productMutationId + " not found !"));

        WarehouseInventory originWarehouseInventory = warehouseInventoryRepository.findByProductIdAndWarehouseIdAndDeletedAtIsNull(productMutation.getProduct().getId(), productMutation.getOriginWarehouse().getId())
                .orElseThrow(()-> new WarehouseInventoryNotFoundException("Origin warehouse inventory not found "));

        // roll back quantity from origin warehouse
        originWarehouseInventory.setQuantity(originWarehouseInventory.getQuantity() + productMutation.getQuantity());
        warehouseInventoryRepository.save(originWarehouseInventory);

        // update product mutation to completed
        productMutation.setReviewer(reviewer);
        productMutation.setReviewerNotes(req.getReviewerNotes());
        productMutation.setReviewedAt(OffsetDateTime.now());
        productMutation.setProductMutationStatus(productMutationStatusDeclined);

        return new ProductMutationResponseDTO(productMutationRepository.save(productMutation));
    }
}
