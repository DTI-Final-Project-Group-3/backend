package com.warehub.warehub.infrastructure.productMutation.controller;

import com.warehub.warehub.common.response.ApiResponse;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationApproveRequestDTO;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationPaginationRequestDTO;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationRequestDTO;
import com.warehub.warehub.usecase.productMutation.CreateProductMutationUseCase;
import com.warehub.warehub.usecase.productMutation.DeleteProductMutationUseCase;
import com.warehub.warehub.usecase.productMutation.GetProductMutationUseCase;
import com.warehub.warehub.usecase.productMutation.UpdateProductMutationUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                                                         @RequestParam Long mutationTypeId){
        ProductMutationPaginationRequestDTO requestDTO = new ProductMutationPaginationRequestDTO(page, limit, originWarehouseId, destinationWarehouseId, mutationTypeId);
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Get product mutation success", getProductMutationUseCase.getPaginatedProductMutationByWarehouseId(requestDTO));
    }

    @GetMapping("/{productMutationId}")
    public ResponseEntity<?> getProductMutationById(@PathVariable Long productMutationId){
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Get product mutation by id success", getProductMutationUseCase.getProductMutationById(productMutationId));
    }

    @PutMapping("/manual/{productMutationId}")
    public ResponseEntity<?> approveManualProductMutation(@PathVariable Long productMutationId, @RequestBody ProductMutationApproveRequestDTO req){
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Approve manual product mutation success !", updateProductMutationUseCase.approveManualProductMutation(productMutationId, req));
    }

    @DeleteMapping("/{productMutationId}")
    public ResponseEntity<?> deleteProductMutationById(@PathVariable Long productMutationId){
        deleteProductMutationUseCase.deleteProductMutationById(productMutationId);
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Delete product mutation success");
    }

}
