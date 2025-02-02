package com.warehub.warehub.infrastructure.product.controller;

import com.warehub.warehub.common.response.ApiResponse;
import com.warehub.warehub.infrastructure.product.dto.ProductCategoryRequestDTO;
import com.warehub.warehub.infrastructure.product.dto.ProductRequestDTO;
import com.warehub.warehub.usecase.product.CreateProductCategoryUseCase;
import com.warehub.warehub.usecase.product.CreateProductUseCase;
import com.warehub.warehub.usecase.product.GetProductCategoryUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final CreateProductCategoryUseCase createProductCategoryUseCase;
    private final CreateProductUseCase createProductUseCase;
    private final GetProductCategoryUseCase getProductCategoryUseCase;

    public ProductController(CreateProductCategoryUseCase createProductCategoryUseCase, CreateProductUseCase createProductUseCase, GetProductCategoryUseCase getProductCategoryUseCase) {
        this.createProductCategoryUseCase = createProductCategoryUseCase;
        this.createProductUseCase = createProductUseCase;
        this.getProductCategoryUseCase = getProductCategoryUseCase;
    }

    @PostMapping
    public ResponseEntity<?> createProduct(ProductRequestDTO req){
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Create new product success !", createProductUseCase.createProduct(req));
    }

    @PostMapping("/categories")
    public ResponseEntity<?> createProductCategory(ProductCategoryRequestDTO req){
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Create new product category success !", createProductCategoryUseCase.createProductCategory(req));
    }

    @GetMapping("/categories")
    public ResponseEntity<?> getAllProductCategory(){
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Get all product categories success", getProductCategoryUseCase.getAllProductCategory());
    }

    @GetMapping("/categories/{productCategoryId}")
    public ResponseEntity<?> getProductCategoriesById(@PathVariable  Long productCategoryId){
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Get product category by ID success", getProductCategoryUseCase.getProductCategoryById(productCategoryId));
    }
}
