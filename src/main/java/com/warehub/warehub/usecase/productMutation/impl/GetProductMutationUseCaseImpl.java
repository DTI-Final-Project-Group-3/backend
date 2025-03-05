package com.warehub.warehub.usecase.productMutation.impl;

import com.warehub.warehub.common.exceptions.ProductMutationNotFoundException;
import com.warehub.warehub.common.utils.PaginationInfo;
import com.warehub.warehub.entity.ProductMutation;
import com.warehub.warehub.infrastructure.productMutation.dto.*;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationRepository;
import com.warehub.warehub.usecase.productMutation.GetProductMutationUseCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetProductMutationUseCaseImpl implements GetProductMutationUseCase {

    private final ProductMutationRepository productMutationRepository;

    public GetProductMutationUseCaseImpl(ProductMutationRepository productMutationRepository) {
        this.productMutationRepository = productMutationRepository;
    }

    @Override
    public ProductMutationResponseDTO getProductMutationById(Long productMutationId) {

        ProductMutation productMutation = productMutationRepository.findByIdAndDeletedAtIsNull(productMutationId)
                .orElseThrow(()-> new ProductMutationNotFoundException("Product mutation with Id" + productMutationId + " not found !"));

        return new ProductMutationResponseDTO(productMutation);
    }

    @Override
    public PaginationInfo<ProductMutationDetailResponseDTO> getPaginatedProductMutationByWarehouseId(ProductMutationPaginationRequestDTO req) {
        PageRequest pageRequest = PageRequest.of(req.getPage(), req.getLimit());

        Page<ProductMutationDetailResponseDTO> responseDTOS = productMutationRepository.findByWarehouseIdDTO(req.getOriginWarehouseId(), req.getDestinationWarehouseId(), req.getProductMutationTypeId(), pageRequest);

        return new PaginationInfo<>(responseDTOS, responseDTOS.getContent());
    }

    @Override
    public List<ProductMutationReportResponseDTO> getProductMutationReport(ProductMutationReportRequestDTO req) {

        return productMutationRepository
                .findProductMutationDetailsByDateRange(
                        req.getStartDate(), req.getEndDate(),
                        req.getProductId(), req.getProductCategoryId(),
                        req.getProductMutationTypeId(), req.getProductMutationStatusId());
    }
}
