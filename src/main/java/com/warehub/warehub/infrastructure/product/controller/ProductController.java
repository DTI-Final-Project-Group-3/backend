package com.warehub.warehub.infrastructure.product.controller;

import com.warehub.warehub.common.response.ApiResponse;
import com.warehub.warehub.infrastructure.product.dto.ProductCategoryRequestDTO;
import com.warehub.warehub.infrastructure.product.dto.ProductRequestDTO;
import com.warehub.warehub.usecase.product.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final CreateProductCategoryUseCase createProductCategoryUseCase;
    private final GetProductCategoryUseCase getProductCategoryUseCase;
    private final UpdateProductCategoryUseCase updateProductCategoryUseCase;
    private final DeleteProductCategoryUseCase deleteProductCategoryUseCase;

    private final CreateProductUseCase createProductUseCase;
    private final GetProductUseCase getProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final DeleteProductUseCase deleteProductUseCase;

    public ProductController(CreateProductCategoryUseCase createProductCategoryUseCase, GetProductCategoryUseCase getProductCategoryUseCase, UpdateProductCategoryUseCase updateProductCategoryUseCase, DeleteProductCategoryUseCase deleteProductCategoryUseCase, GetProductUseCase getProductUseCase, CreateProductUseCase createProductUseCase, UpdateProductUseCase updateProductUseCase, DeleteProductUseCase deleteProductUseCase) {
        this.createProductCategoryUseCase = createProductCategoryUseCase;
        this.getProductCategoryUseCase = getProductCategoryUseCase;
        this.updateProductCategoryUseCase = updateProductCategoryUseCase;
        this.deleteProductCategoryUseCase = deleteProductCategoryUseCase;
        this.createProductUseCase = createProductUseCase;
        this.getProductUseCase = getProductUseCase;
        this.updateProductUseCase = updateProductUseCase;
        this.deleteProductUseCase = deleteProductUseCase;
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody ProductRequestDTO req){
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Create new product success !", createProductUseCase.createProduct(req));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllProduct(){
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Get product by Id success", getProductUseCase.getAllProduct());
    }

    @GetMapping()
    public ResponseEntity<?> getPaginatedProduct(@RequestParam int page,
                                                 @RequestParam int limit,
                                                 @RequestParam(required = false) double lng,
                                                 @RequestParam(required = false) double lat,
                                                 @RequestParam(required = false) Long cat,
                                                 @RequestParam(required = false) String search) {
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Get all product success", getProductUseCase.getPaginatedProducts(page, limit, lng, lat, cat, search));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductById(@PathVariable Long productId){
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Get product by ID success", getProductUseCase.getProductById(productId));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<?> updateProductById(@PathVariable Long productId,
                                               @RequestBody ProductRequestDTO req){
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Update product success", updateProductUseCase.updateProductById(productId, req));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProductById(@PathVariable Long productId){
        deleteProductUseCase.deleteProductById(productId);
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Delete product success");
    }

    @PostMapping("/categories")
    public ResponseEntity<?> createProductCategory(@RequestBody ProductCategoryRequestDTO req){
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Create new product category success !", createProductCategoryUseCase.createProductCategory(req));
    }

    @GetMapping("/categories/all")
    public ResponseEntity<?> getAllProductCategory(){
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Get all product categories success", getProductCategoryUseCase.getAllProductCategory());
    }

    @GetMapping("/categories/{productCategoryId}")
    public ResponseEntity<?> getProductCategoriesById(@PathVariable  Long productCategoryId){
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Get product category by ID success", getProductCategoryUseCase.getProductCategoryById(productCategoryId));
    }

    @PutMapping("/categories/{productCategoryId}")
    public ResponseEntity<?> updateProductCategoryById(@PathVariable Long productCategoryId,
                                                       @RequestBody ProductCategoryRequestDTO req){
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Update product success", updateProductCategoryUseCase.updateProductCategoryById(productCategoryId, req));
    }

    @DeleteMapping("/categories/{productCategoryId}")
    public ResponseEntity<?> deleteProductCategoryById(@PathVariable Long productCategoryId){
        deleteProductCategoryUseCase.deleteProductCategoryById(productCategoryId);
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Delete product category success");
    }
}
