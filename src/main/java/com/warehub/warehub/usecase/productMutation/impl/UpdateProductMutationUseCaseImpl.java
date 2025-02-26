package com.warehub.warehub.usecase.productMutation.impl;

import com.warehub.warehub.common.exceptions.*;
import com.warehub.warehub.entity.*;
import com.warehub.warehub.infrastructure.product.repository.ProductRepository;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationApproveRequestDTO;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationRequestDTO;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationResponseDTO;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationRepository;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationStatusRepository;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationTypeRepository;
import com.warehub.warehub.infrastructure.users.repository.UsersRepository;
import com.warehub.warehub.infrastructure.warehouse.repository.WarehouseRepository;
import com.warehub.warehub.usecase.productMutation.UpdateProductMutationUseCase;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class UpdateProductMutationUseCaseImpl implements UpdateProductMutationUseCase {

    private final ProductMutationRepository productMutationRepository;
    private final ProductMutationStatusRepository productMutationStatusRepository;
    private final UsersRepository usersRepository;

    public UpdateProductMutationUseCaseImpl(ProductMutationRepository productMutationRepository, ProductMutationStatusRepository productMutationStatusRepository, UsersRepository usersRepository) {
        this.productMutationRepository = productMutationRepository;
        this.productMutationStatusRepository = productMutationStatusRepository;
        this.usersRepository = usersRepository;
    }

    @Override
    public ProductMutationResponseDTO approveManualProductMutation(Long productMutationId, ProductMutationApproveRequestDTO req) {
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
