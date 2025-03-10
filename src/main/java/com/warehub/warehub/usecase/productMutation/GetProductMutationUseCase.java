package com.warehub.warehub.usecase.productMutation;

import com.warehub.warehub.common.utils.PaginationInfo;
import com.warehub.warehub.infrastructure.productMutation.dto.*;

import java.util.List;


public interface GetProductMutationUseCase {
    ProductMutationResponseDTO getProductMutationById(Long productMutationId);
    PaginationInfo<ProductMutationDetailResponseDTO> getPaginatedProductMutationByWarehouseId(ProductMutationPaginationRequestDTO req);

    // report
    PaginationInfo<ProductMutationHistoryResponseDTO> getProductMutationHistory(ProductMutationHistoryRequestDTO req);
    ProductMutationTotalResponseDTO getTotalProductMutation(ProductMutationHistoryRequestDTO req);
    List<ProductMutationDailySummaryResponseDTO> getDailyMutationSummary(ProductMutationHistoryRequestDTO req);

    List<ProductMutationTypeResponseDTO> getAllProductMutationType();
    List<ProductMutationStatusResponseDTO> getAllProductMutationStatus();
}
