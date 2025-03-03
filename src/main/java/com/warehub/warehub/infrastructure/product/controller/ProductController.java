package com.warehub.warehub.infrastructure.product.controller;

import com.warehub.warehub.common.response.ApiResponse;
import com.warehub.warehub.infrastructure.product.dto.ProductNearbyRequestDTO;
import com.warehub.warehub.infrastructure.product.dto.ProductPaginationRequestDTO;
import com.warehub.warehub.infrastructure.product.dto.ProductCategoryRequestDTO;
import com.warehub.warehub.infrastructure.product.dto.ProductRequestDTO;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationReportRequestDTO;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationReportResponseDTO;
import com.warehub.warehub.usecase.product.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

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

    @GetMapping
    public ResponseEntity<?> getPaginatedProducts(@RequestParam int page,
                                                 @RequestParam int limit,
                                                 @RequestParam(required = false) Long productCategoryId,
                                                 @RequestParam(required = false) String searchQuery){
        ProductPaginationRequestDTO requestDTO = new ProductPaginationRequestDTO(page, limit, productCategoryId, searchQuery);
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Get products success", getProductUseCase.getPaginatedProducts(requestDTO));
    }

    @GetMapping(value = "/nearby", params = {"page", "limit"})
    public ResponseEntity<?> getPaginatedNearbyProducts(@RequestParam int page,
                                                    @RequestParam int limit,
                                                    @RequestParam(required = false) Double longitude,
                                                    @RequestParam(required = false) Double latitude,
                                                    @RequestParam(required = false) Double radius,
                                                    @RequestParam(required = false) Long productCategoryId,
                                                    @RequestParam(required = false) String searchQuery) {
        ProductPaginationRequestDTO requestDTO = new ProductPaginationRequestDTO(page, limit, longitude, latitude, radius, productCategoryId, searchQuery);
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Get nearby products success", getProductUseCase.getPaginatedNearbyProducts(requestDTO));
    }

    @GetMapping(value = "/nearby", params = "productId")
    public ResponseEntity<?> getNearbyProductById(@RequestParam(required = false) Double longitude,
                                                @RequestParam(required = false) Double latitude,
                                                @RequestParam(required = false) Double radius,
                                                @RequestParam(required = false) Long productId) {
        ProductNearbyRequestDTO requestDTO = new ProductNearbyRequestDTO(longitude, latitude, radius, productId);
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Get nearby products success", getProductUseCase.getNearbyProductById(requestDTO));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllProduct(){
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Get product by Id success", getProductUseCase.getAllProductList());
    }

    @GetMapping("/filter/include")
    public ResponseEntity<?> getProductByWarehouseId(@RequestParam Long warehouseId){
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Get products with include filter success", getProductUseCase.getProductsIncludeFilter(warehouseId));
    }

    @GetMapping("/filter/exclude")
    public ResponseEntity<?> getProductExcludeFilter(@RequestParam Long warehouseId){
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Get products with exclude filter success !", getProductUseCase.getProductsExcludeFilter(warehouseId));
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
