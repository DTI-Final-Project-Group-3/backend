package com.warehub.warehub.usecase.productMutation;

import com.warehub.warehub.common.utils.PaginationInfo;
import com.warehub.warehub.infrastructure.productMutation.dto.*;

import java.util.List;


public interface GetProductMutationUseCase {
    ProductMutationResponseDTO getProductMutationById(Long productMutationId);
    PaginationInfo<ProductMutationDetailResponseDTO> getPaginatedProductMutationByWarehouseId(ProductMutationPaginationRequestDTO req);
    List<ProductMutationReportResponseDTO> getProductMutationReport(ProductMutationReportRequestDTO req);
    ProductMutationTotalResponseDTO getTotalProductMutation(ProductMutationReportRequestDTO req);
    List<ProductMutationDailySummaryResponseDTO> getDailyMutationSummary(ProductMutationReportRequestDTO req);
}
