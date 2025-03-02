package com.warehub.warehub.infrastructure.productMutation.service;

import com.warehub.warehub.common.exceptions.*;
import com.warehub.warehub.entity.*;
import com.warehub.warehub.infrastructure.product.repository.ProductRepository;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationProcessRequestDTO;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationRequestDTO;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationRepository;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationStatusRepository;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationTypeRepository;
import com.warehub.warehub.infrastructure.users.repository.UsersRepository;
import com.warehub.warehub.infrastructure.warehouse.repository.WarehouseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.OffsetDateTime;

@Service
public class ProductMutationService {

    private final ProductRepository productRepository;
    private final UsersRepository usersRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductMutationTypeRepository productMutationTypeRepository;
    private final ProductMutationStatusRepository productMutationStatusRepository;
    private final ProductMutationRepository productMutationRepository;

    public ProductMutationService(ProductRepository productRepository, UsersRepository usersRepository, WarehouseRepository warehouseRepository, ProductMutationTypeRepository productMutationTypeRepository, ProductMutationStatusRepository productMutationStatusRepository, ProductMutationRepository productMutationRepository) {
        this.productRepository = productRepository;
        this.usersRepository = usersRepository;
        this.warehouseRepository = warehouseRepository;
        this.productMutationTypeRepository = productMutationTypeRepository;
        this.productMutationStatusRepository = productMutationStatusRepository;
        this.productMutationRepository = productMutationRepository;
    }

    private Product validateAndGetProduct(Long productId) {
        return productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + productId + " not found!"));
    }
    private User validateAndGetUser(Long userId) {
        return usersRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User with ID " + userId + " not found!"));
    }
    private Warehouse validateAndGetWarehouse(Long warehouseId, String warehouseType) {
        return warehouseRepository.findByIdAndDeletedAtIsNull(warehouseId)
                .orElseThrow(() -> new WarehouseNotFoundException(warehouseType + " warehouse with ID " + warehouseId + " not found!"));
    }
    private ProductMutationType getProductMutationTypeById(Long typeId) {
        return productMutationTypeRepository.findByIdAndDeletedAtIsNull(typeId)
                .orElseThrow(() -> new ProductMutationTypeNotFoundException("Product mutation type with ID " + typeId + " not found!"));
    }
    private ProductMutationStatus getProductMutationStatusById(Long statusId) {
        return productMutationStatusRepository.findByIdAndDeletedAtIsNull(statusId)
                .orElseThrow(() -> new ProductMutationStatusNotFoundException("Product mutation status with ID " + statusId + " not found!"));
    }

    @Transactional
    public ProductMutation createAutoMutation(ProductMutationRequestDTO req, Long typeId, Long statusId) {

        Product product = validateAndGetProduct(req.getProductId());
        User requester = validateAndGetUser(req.getRequesterId());
        Warehouse originWarehouse = validateAndGetWarehouse(req.getOriginWarehouseId(), "Origin");
        Warehouse destinationWarehouse = validateAndGetWarehouse(req.getDestinationWarehouseId(), "Destination");

        ProductMutationType productMutationType = getProductMutationTypeById(typeId);
        ProductMutationStatus productMutationStatus = getProductMutationStatusById(statusId);

        ProductMutation productMutation = new ProductMutation();
        productMutation.setProduct(product);
        productMutation.setQuantity(req.getQuantity());
        productMutation.setReviewerNotes(req.getRequesterNotes());
        productMutation.setRequester(requester);
        productMutation.setOriginWarehouse(originWarehouse);
        productMutation.setDestinationWarehouse(destinationWarehouse);
        productMutation.setProductMutationType(productMutationType);
        productMutation.setProductMutationStatus(productMutationStatus);

        return productMutationRepository.save(productMutation);
    }

    @Transactional
    public ProductMutation createManualMutation(ProductMutationRequestDTO req, Long typeId, Long statusId) {

        Product product = validateAndGetProduct(req.getProductId());
        User requester = validateAndGetUser(req.getRequesterId());
        Warehouse destinationWarehouse = validateAndGetWarehouse(req.getDestinationWarehouseId(), "Destination");

        ProductMutationType productMutationType = getProductMutationTypeById(typeId);
        ProductMutationStatus productMutationStatus = getProductMutationStatusById(statusId);

        ProductMutation productMutation = new ProductMutation();
        productMutation.setProduct(product);
        productMutation.setQuantity(req.getQuantity());
        productMutation.setReviewerNotes(req.getRequesterNotes());
        productMutation.setRequester(requester);
        productMutation.setDestinationWarehouse(destinationWarehouse);
        productMutation.setProductMutationType(productMutationType);
        productMutation.setProductMutationStatus(productMutationStatus);

        return productMutationRepository.save(productMutation);
    }

    @Transactional
    public ProductMutation processManualMutation(Long mutationId, ProductMutationProcessRequestDTO req, Long typeId, Long statusId) {

        ProductMutation existingMutation = productMutationRepository.findByIdAndDeletedAtIsNull(mutationId)
                .orElseThrow(() -> new ProductMutationNotFoundException("Product mutation with ID " + mutationId + " not found!"));

        if (req.getUserId() != null && !req.getUserId().equals(existingMutation.getReviewer().getId())) {
            existingMutation.setRequester(validateAndGetUser(req.getUserId()));
        }
        if (req.getNotes() != null) {
            existingMutation.setRequesterNotes(req.getNotes());
        }
        if (typeId != null) {
            existingMutation.setProductMutationType(getProductMutationTypeById(typeId));
        }
        if (statusId != null) {
            existingMutation.setProductMutationStatus(getProductMutationStatusById(statusId));
        }
        existingMutation.setReviewedAt(OffsetDateTime.now());
        existingMutation.setUpdatedAt(OffsetDateTime.now());

        return productMutationRepository.save(existingMutation);
    }
}