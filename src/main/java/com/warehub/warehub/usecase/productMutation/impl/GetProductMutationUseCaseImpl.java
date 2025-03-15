package com.warehub.warehub.usecase.productMutation.impl;

import com.warehub.warehub.common.utils.DateConverter;
import com.warehub.warehub.common.utils.PaginationInfo;
import com.warehub.warehub.common.utils.ValidationService;
import com.warehub.warehub.entity.ProductMutation;
import com.warehub.warehub.infrastructure.productMutation.dto.*;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationRepository;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationStatusRepository;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationTypeRepository;
import com.warehub.warehub.usecase.productMutation.GetProductMutationUseCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class GetProductMutationUseCaseImpl implements GetProductMutationUseCase {

    private final ValidationService validationService;
    private final ProductMutationRepository productMutationRepository;
    private final ProductMutationTypeRepository productMutationTypeRepository;
    private final ProductMutationStatusRepository productMutationStatusRepository;

    public GetProductMutationUseCaseImpl(ValidationService validationService, ProductMutationRepository productMutationRepository, ProductMutationTypeRepository productMutationTypeRepository, ProductMutationStatusRepository productMutationStatusRepository) {
        this.validationService = validationService;
        this.productMutationRepository = productMutationRepository;
        this.productMutationTypeRepository = productMutationTypeRepository;
        this.productMutationStatusRepository = productMutationStatusRepository;
    }

    @Override
    public ProductMutationResponseDTO getProductMutationById(Long productMutationId) {

        ProductMutation productMutation = validationService.validateProductMutationId(productMutationId);

        return new ProductMutationResponseDTO(productMutation);
    }

    @Override
    public PaginationInfo<ProductMutationDetailResponseDTO> getPaginatedProductMutationByWarehouseId(ProductMutationPaginationRequestDTO req) {
        PageRequest pageRequest = PageRequest.of(req.getPage(), req.getLimit());

        validationService.validateDateRange(req.getStartDate(), req.getEndDate());
        validationService.validateWarehouseId(req.getOriginWarehouseId(), "Origin warehouse");
        validationService.validateWarehouseId(req.getDestinationWarehouseId(), "Destination warehouse");
        validationService.validateDateRange(req.getStartDate(), req.getEndDate());
        for (Long id : req.getProductMutationTypeId()){
            validationService.validateProductMutationTypeId(id);
        }

        OffsetDateTime startDate = DateConverter.convertStarDate(req.getStartDate());
        OffsetDateTime endDate = DateConverter.convertEndDate(req.getEndDate());

        Page<ProductMutationDetailResponseDTO> responseDTOS = productMutationRepository
                .findByWarehouseIdDTO(startDate, endDate,
                        req.getProductId(), req. getProductCategoryId(),
                        req.getOriginWarehouseId(), req.getDestinationWarehouseId(),
                        req.getProductMutationTypeId(), req.getProductMutationStatusId(),
                        pageRequest);

        return new PaginationInfo<>(responseDTOS, responseDTOS.getContent());
    }

    @Override
    public PaginationInfo<ProductMutationHistoryResponseDTO> getProductMutationHistory(ProductMutationHistoryRequestDTO req) {

        PageRequest pageRequest = PageRequest.of(req.getPage(), req.getLimit());

        validationService.validateDateRange(req.getStartDate(), req.getEndDate());
        validationService.validateProductId(req.getProductId());
        validationService.validateProductCategoryId(req.getProductCategoryId());
        validationService.validateProductMutationTypeId(req.getProductMutationTypeId());
        validationService.validateProductMutationStatusId(req.getProductMutationStatusId());
        validationService.validateWarehouseId(req.getDestinationWarehouseId(), "Warehouse");

        OffsetDateTime startDate = DateConverter.convertStarDate(req.getStartDate());
        OffsetDateTime endDate = DateConverter.convertEndDate(req.getEndDate());

        Page<ProductMutationHistoryResponseDTO> responseDTOS = productMutationRepository
                .findProductMutationDetailsByDateRange(
                        startDate, endDate,
                        req.getProductId(), req.getProductCategoryId(),
                        req.getProductMutationTypeId(), req.getProductMutationStatusId(),
                        req.getDestinationWarehouseId(),
                        pageRequest);

        return new PaginationInfo<>(responseDTOS, responseDTOS.getContent());
    }

    @Override
    public ProductMutationTotalResponseDTO getTotalProductMutation(ProductMutationHistoryRequestDTO req) {

        validationService.validateDateRange(req.getStartDate(), req.getEndDate());
        validationService.validateProductId(req.getProductId());
        validationService.validateProductCategoryId(req.getProductCategoryId());
        validationService.validateProductMutationTypeId(req.getProductMutationTypeId());
        validationService.validateProductMutationStatusId(req.getProductMutationStatusId());
        validationService.validateWarehouseId(req.getDestinationWarehouseId(), "Warehouse");

        OffsetDateTime startDate = DateConverter.convertStarDate(req.getStartDate());
        OffsetDateTime endDate = DateConverter.convertEndDate(req.getEndDate());

        return productMutationRepository
                .calculateProductQuantityMetricsByDateRange(
                        startDate, endDate,
                        req.getProductId(), req.getProductCategoryId(),
                        req.getProductMutationTypeId(), req.getProductMutationStatusId(),
                        req.getDestinationWarehouseId());
    }

    @Override
    public List<ProductMutationDailySummaryResponseDTO> getDailyMutationSummary(ProductMutationHistoryRequestDTO req) {

        validationService.validateDateRange(req.getStartDate(), req.getEndDate());
        validationService.validateProductId(req.getProductId());
        validationService.validateProductCategoryId(req.getProductCategoryId());
        validationService.validateProductMutationTypeId(req.getProductMutationTypeId());
        validationService.validateProductMutationStatusId(req.getProductMutationStatusId());
        validationService.validateWarehouseId(req.getDestinationWarehouseId(), "Warehouse");

        OffsetDateTime startDate = DateConverter.convertStarDate(req.getStartDate());
        OffsetDateTime endDate = DateConverter.convertEndDate(req.getEndDate());

        return productMutationRepository
                .findDailyMutationSummary(startDate, endDate,
                        req.getProductId(), req.getProductCategoryId(),
                        req.getProductMutationTypeId(), req.getProductMutationStatusId(),
                        req.getDestinationWarehouseId());
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
