package com.warehub.warehub.infrastructure.product.controller;

import com.warehub.warehub.common.response.ApiResponse;
import com.warehub.warehub.infrastructure.product.dto.ProductCategoryRequestDTO;
import com.warehub.warehub.infrastructure.product.dto.ProductRequestDTO;
import com.warehub.warehub.usecase.product.CreateProductCategoryUseCase;
import com.warehub.warehub.usecase.product.CreateProductUseCase;
import com.warehub.warehub.usecase.product.GetProductCategoryUseCase;
import com.warehub.warehub.usecase.product.GetProductUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/product")
public class ProductController {

    private final CreateProductCategoryUseCase createProductCategoryUseCase;
    private final CreateProductUseCase createProductUseCase;
    private final GetProductCategoryUseCase getProductCategoryUseCase;
    private final GetProductUseCase getProductUseCase;

    public ProductController(CreateProductCategoryUseCase createProductCategoryUseCase, CreateProductUseCase createProductUseCase, GetProductCategoryUseCase getProductCategoryUseCase, GetProductUseCase getProductUseCase) {
        this.createProductCategoryUseCase = createProductCategoryUseCase;
        this.createProductUseCase = createProductUseCase;
        this.getProductCategoryUseCase = getProductCategoryUseCase;
        this.getProductUseCase = getProductUseCase;
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

    @PostMapping("/category")
    public ResponseEntity<?> createProductCategory(@RequestBody ProductCategoryRequestDTO req){
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Create new product category success !", createProductCategoryUseCase.createProductCategory(req));
    }

    @GetMapping("/category/all")
    public ResponseEntity<?> getAllProductCategory(){
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Get all product categories success", getProductCategoryUseCase.getAllProductCategory());
    }

    @GetMapping("/category/{productCategoryId}")
    public ResponseEntity<?> getProductCategoriesById(@PathVariable  Long productCategoryId){
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Get product category by ID success", getProductCategoryUseCase.getProductCategoryById(productCategoryId));
    }
}
