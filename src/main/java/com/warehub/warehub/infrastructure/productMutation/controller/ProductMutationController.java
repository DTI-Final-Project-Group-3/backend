package com.warehub.warehub.infrastructure.productMutation.controller;

import com.warehub.warehub.common.response.ApiResponse;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationProcessRequestDTO;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationPaginationRequestDTO;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationHistoryRequestDTO;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationRequestDTO;
import com.warehub.warehub.usecase.productMutation.CreateProductMutationUseCase;
import com.warehub.warehub.usecase.productMutation.DeleteProductMutationUseCase;
import com.warehub.warehub.usecase.productMutation.GetProductMutationUseCase;
import com.warehub.warehub.usecase.productMutation.UpdateProductMutationUseCase;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products/mutations")
public class ProductMutationController {

    private final CreateProductMutationUseCase createProductMutationUseCase;
    private final GetProductMutationUseCase getProductMutationUseCase;
    private final UpdateProductMutationUseCase updateProductMutationUseCase;
    private final DeleteProductMutationUseCase deleteProductMutationUseCase;

    public ProductMutationController(CreateProductMutationUseCase createProductMutationUseCase, GetProductMutationUseCase getProductMutationUseCase, UpdateProductMutationUseCase updateProductMutationUseCase, DeleteProductMutationUseCase deleteProductMutationUseCase) {
        this.createProductMutationUseCase = createProductMutationUseCase;
        this.getProductMutationUseCase = getProductMutationUseCase;
        this.updateProductMutationUseCase = updateProductMutationUseCase;
        this.deleteProductMutationUseCase = deleteProductMutationUseCase;
    }

    @PostMapping("/manual")
    public ResponseEntity<?> createManualProductMutation(@RequestBody ProductMutationRequestDTO req){
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Create manual product mutation success", createProductMutationUseCase.createManualMutation(req));
    }

    @PostMapping("/auto")
    public ResponseEntity<?> createAutoProductMutation(@RequestBody ProductMutationRequestDTO req){
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Create auto product mutation success", createProductMutationUseCase.createAutoMutation(req));
    }

    @GetMapping
    public ResponseEntity<?> getPaginatedProductMutation(@RequestParam int page,
                                                         @RequestParam int limit,
                                                         @RequestParam(required = false) Long originWarehouseId,
                                                         @RequestParam(required = false) Long destinationWarehouseId,
                                                         @RequestParam List<Long> productMutationTypeId){
        ProductMutationPaginationRequestDTO requestDTO = new ProductMutationPaginationRequestDTO(page, limit, originWarehouseId, destinationWarehouseId, productMutationTypeId);
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Get product mutation success", getProductMutationUseCase.getPaginatedProductMutationByWarehouseId(requestDTO));
    }

    @GetMapping("/history")
    public ResponseEntity<?> getProductMutationReport(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startedAt,
                                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endedAt,
                                                      @RequestParam(required = false) Long productId,
                                                      @RequestParam(required = false) Long productCategoryId,
                                                      @RequestParam(required = false) Long productMutationTypeId,
                                                      @RequestParam(required = false) Long productMutationStatusId,
                                                      @RequestParam(required = false) Long warehouseId){

        ProductMutationHistoryRequestDTO requestDTO = new ProductMutationHistoryRequestDTO(startedAt, endedAt, productId, productCategoryId, productMutationTypeId, productMutationStatusId, warehouseId);
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Get product mutation success", getProductMutationUseCase.getProductMutationHistory(requestDTO));

    }

    @GetMapping("/total")
    public ResponseEntity<?> getProductMutationTotal(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startedAt,
                                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endedAt,
                                                     @RequestParam(required = false) Long productId,
                                                     @RequestParam(required = false) Long productCategoryId,
                                                     @RequestParam(required = false) Long productMutationTypeId,
                                                     @RequestParam(required = false) Long productMutationStatusId,
                                                     @RequestParam(required = false) Long warehouseId
                                                     ) {
        ProductMutationHistoryRequestDTO requestDTO = new ProductMutationHistoryRequestDTO(startedAt, endedAt, productId, productCategoryId, productMutationTypeId, productMutationStatusId, warehouseId);
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Get total summary products success", getProductMutationUseCase.getTotalProductMutation(requestDTO));
    }

    @GetMapping("/daily")
    public ResponseEntity<?> getProductMutationDailySummary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startedAt,
                                                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endedAt,
                                                            @RequestParam(required = false) Long productId,
                                                            @RequestParam(required = false) Long productCategoryId,
                                                            @RequestParam(required = false) Long productMutationTypeId,
                                                            @RequestParam(required = false) Long productMutationStatusId,
                                                            @RequestParam(required = false) Long warehouseId) {
        ProductMutationHistoryRequestDTO requestDTO = new ProductMutationHistoryRequestDTO(startedAt, endedAt, productId, productCategoryId, productMutationTypeId, productMutationStatusId, warehouseId);
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Get daily summary product mutation success", getProductMutationUseCase.getDailyMutationSummary(requestDTO));
    }

    @GetMapping("/types")
    public ResponseEntity<?> getALlProductMutationType(){
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Get all product mutation type", getProductMutationUseCase.getAllProductMutationType());
    }

    @GetMapping("/statuses")
    public ResponseEntity<?> getALlProductMutationStatus(){
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Get all product mutation status ", getProductMutationUseCase.getAllProductMutationStatus());
    }


    @GetMapping("/{productMutationId}")
    public ResponseEntity<?> getProductMutationById(@PathVariable Long productMutationId){
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Get product mutation by id success", getProductMutationUseCase.getProductMutationById(productMutationId));
    }

    @PutMapping("/manual/approve/{productMutationId}")
    public ResponseEntity<?> approveManualProductMutation(@PathVariable Long productMutationId, @RequestBody ProductMutationProcessRequestDTO req){
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Approve manual product mutation success !", updateProductMutationUseCase.approveManualProductMutation(productMutationId, req));
    }

    @PutMapping("/manual/decline/{productMutationId}")
    public ResponseEntity<?> declineManualProductMutation(@PathVariable Long productMutationId, @RequestBody ProductMutationProcessRequestDTO req){
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Approve manual product mutation success !", updateProductMutationUseCase.declineManualProductMutation(productMutationId, req));
    }

    @DeleteMapping("/{productMutationId}")
    public ResponseEntity<?> deleteProductMutationById(@PathVariable Long productMutationId){
        deleteProductMutationUseCase.deleteProductMutationById(productMutationId);
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Delete product mutation success");
    }

}
