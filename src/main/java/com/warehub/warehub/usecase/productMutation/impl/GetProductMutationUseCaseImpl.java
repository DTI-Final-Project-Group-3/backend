package com.warehub.warehub.usecase.productMutation.impl;

import com.warehub.warehub.common.exceptions.ProductMutationNotFoundException;
import com.warehub.warehub.common.utils.PaginationInfo;
import com.warehub.warehub.entity.ProductMutation;
import com.warehub.warehub.entity.ProductMutationStatus;
import com.warehub.warehub.entity.ProductMutationType;
import com.warehub.warehub.infrastructure.productMutation.dto.*;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationRepository;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationStatusRepository;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationTypeRepository;
import com.warehub.warehub.usecase.productMutation.GetProductMutationUseCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetProductMutationUseCaseImpl implements GetProductMutationUseCase {

    private final ProductMutationRepository productMutationRepository;
    private final ProductMutationTypeRepository productMutationTypeRepository;
    private final ProductMutationStatusRepository productMutationStatusRepository;

    public GetProductMutationUseCaseImpl(ProductMutationRepository productMutationRepository, ProductMutationTypeRepository productMutationTypeRepository, ProductMutationStatusRepository productMutationStatusRepository) {
        this.productMutationRepository = productMutationRepository;
        this.productMutationTypeRepository = productMutationTypeRepository;
        this.productMutationStatusRepository = productMutationStatusRepository;
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
    public List<ProductMutationHistoryResponseDTO> getProductMutationHistory(ProductMutationHistoryRequestDTO req) {

        return productMutationRepository
                .findProductMutationDetailsByDateRange(
                        req.getStartDate(), req.getEndDate(),
                        req.getProductId(), req.getProductCategoryId(),
                        req.getProductMutationTypeId(), req.getProductMutationStatusId(),
                        req.getWarehouseId());
    }

    @Override
    public ProductMutationTotalResponseDTO getTotalProductMutation(ProductMutationHistoryRequestDTO req) {
        return productMutationRepository
                .calculateProductQuantityMetricsByDateRange(
                        req.getStartDate(), req.getEndDate(),
                        req.getProductId(), req.getProductCategoryId(),
                        req.getProductMutationTypeId(), req.getProductMutationStatusId(),
                        req.getWarehouseId());
    }

    @Override
    public List<ProductMutationDailySummaryResponseDTO> getDailyMutationSummary(ProductMutationHistoryRequestDTO req) {
        return productMutationRepository
                .findDailyMutationSummary(req.getStartDate(), req.getEndDate(),
                        req.getProductId(), req.getProductCategoryId(),
                        req.getProductMutationTypeId(), req.getProductMutationStatusId(),
                        req.getWarehouseId());
    }

    @Override
    public List<ProductMutationTypeResponseDTO> getAllProductMutationType() {
        return productMutationTypeRepository.findAllAndDeletedAtIsNull();
    }

    @Override
    public List<ProductMutationStatusResponseDTO> getAllProductMutationStatus() {
        return productMutationStatusRepository.findAllAndDeletedAtIsNull();
    }
}
