package com.warehub.warehub.common.exceptions;

import com.warehub.warehub.common.response.ApiResponse;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Log
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<?>handleDataNotFoundException(DataNotFoundException ex){
        return ApiResponse.failedResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler(MaxListSizeExceededException.class)
    public ResponseEntity<?>handleMaxListSizeExceededException(MaxListSizeExceededException ex){
        return ApiResponse.failedResponse(HttpStatus.PAYLOAD_TOO_LARGE.value(), ex.getMessage());
    }

    @ExceptionHandler(DuplicateProductCategoryException.class)
    public ResponseEntity<?>handleDuplicateProductCategoryException(DuplicateProductCategoryException ex){
        return ApiResponse.failedResponse(HttpStatus.CONFLICT.value(), ex.getMessage());
    }

    @ExceptionHandler(ProductCategoryNotFoundException.class)
    public ResponseEntity<?>handleProductCategoryNotFoundException(ProductCategoryNotFoundException ex){
        return ApiResponse.failedResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler(DuplicateProductException.class)
    public ResponseEntity<?>handleDuplicateProductException(DuplicateProductException ex){
        return ApiResponse.failedResponse(HttpStatus.CONFLICT.value(), ex.getMessage());
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<?>handleProductNotFoundException(ProductNotFoundException ex){
        return ApiResponse.failedResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler(DuplicateWarehouseException.class)
    public ResponseEntity<?>handleDuplicateWarehouseException(DuplicateWarehouseException ex){
        return ApiResponse.failedResponse(HttpStatus.CONFLICT.value(), ex.getMessage());
    }

    @ExceptionHandler(WarehouseNotFoundException.class)
    public ResponseEntity<?>handleWarehouseNotFoundException(WarehouseNotFoundException ex){
        return ApiResponse.failedResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler(DuplicateWarehouseInventoryException.class)
    public ResponseEntity<?>handleDuplicateWarehouseInventoryException(DuplicateWarehouseInventoryException ex){
        return ApiResponse.failedResponse(HttpStatus.CONFLICT.value(), ex.getMessage());
    }

    @ExceptionHandler(WarehouseInventoryNotFoundException.class)
    public ResponseEntity<?>handleWarehouseInventoryNotFoundException(WarehouseInventoryNotFoundException ex){
        return ApiResponse.failedResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler(ProductMutationTypeNotFoundException.class)
    public ResponseEntity<?>handleProductMutationTypeNotFoundException(ProductMutationTypeNotFoundException ex){
        return ApiResponse.failedResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler(ProductMutationStatusNotFoundException.class)
    public ResponseEntity<?>handleProductMutationStatusNotFoundException(ProductMutationStatusNotFoundException ex){
        return ApiResponse.failedResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler(ProductMutationNotFoundException.class)
    public ResponseEntity<?>handleProductMutationNotFoundException(ProductMutationNotFoundException ex){
        return ApiResponse.failedResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler(WarehouseInventoryStatusNotFoundException.class)
    public ResponseEntity<?>handleWarehouseInventoryStatusNotFoundException(WarehouseInventoryStatusNotFoundException ex){
        return ApiResponse.failedResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler(NegativeQuantityException.class)
    public ResponseEntity<?>handleNegativeQuantityException(NegativeQuantityException ex){
        return ApiResponse.failedResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

}
