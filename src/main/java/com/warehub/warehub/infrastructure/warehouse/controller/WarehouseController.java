package com.warehub.warehub.infrastructure.warehouse.controller;

import com.warehub.warehub.common.response.ApiResponse;
import com.warehub.warehub.infrastructure.warehouse.dto.NearbyWarehouseRequestDTO;
import com.warehub.warehub.infrastructure.warehouse.dto.WarehouseRequestDTO;
import com.warehub.warehub.usecase.warehouse.CreateWarehouseUseCase;
import com.warehub.warehub.usecase.warehouse.DeleteWarehouseUseCase;
import com.warehub.warehub.usecase.warehouse.GetWarehouseUseCase;
import com.warehub.warehub.usecase.warehouse.UpdateWarehouseUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/warehouses")
public class WarehouseController {

    private final CreateWarehouseUseCase createWarehouseUseCase;
    private final GetWarehouseUseCase getWarehouseUseCase;
    private final UpdateWarehouseUseCase updateWarehouseUseCase;
    private final DeleteWarehouseUseCase deleteWarehouseUseCase;

    public WarehouseController(CreateWarehouseUseCase createWarehouseUseCase,
                               GetWarehouseUseCase getWarehouseUseCase,
                               UpdateWarehouseUseCase updateWarehouseUseCase,
                               DeleteWarehouseUseCase deleteWarehouseUseCase) {
        this.createWarehouseUseCase = createWarehouseUseCase;
        this.getWarehouseUseCase = getWarehouseUseCase;
        this.updateWarehouseUseCase = updateWarehouseUseCase;
        this.deleteWarehouseUseCase = deleteWarehouseUseCase;
    }

    @PostMapping
    public ResponseEntity<?> createWarehouse(@RequestBody WarehouseRequestDTO req){
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Create warehouse success", createWarehouseUseCase.createWarehouse(req));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllWarehouse(){
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Get all warehouse success", getWarehouseUseCase.getAllWarehouse());
    }

    @GetMapping("/available/all")
    public ResponseEntity<?> getNearbyWarehouseByProductId(@RequestParam Long warehouseId,
                                                           @RequestParam Long productId){
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Get available warehouse success",
                getWarehouseUseCase.getNearbyWarehouseByProductId(warehouseId, productId));
    }

    @GetMapping("/nearby")
    public ResponseEntity<?> getNearbyWarehouses(@RequestParam Double longitude,
                                                 @RequestParam Double latitude,
                                                 @RequestParam(required = false) Long productId){
        NearbyWarehouseRequestDTO nearbyWarehouseRequestDTO = new NearbyWarehouseRequestDTO(longitude, latitude, productId);
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Get nearby warehouse success",
                getWarehouseUseCase.getNearbyWarehouses(nearbyWarehouseRequestDTO));
    }

    @GetMapping("/{warehouseId}")
    public ResponseEntity<?> getWarehouseById(@PathVariable Long warehouseId){
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Get warehouse by ID success",
                getWarehouseUseCase.getWarehouseById(warehouseId));
    }

    @PutMapping("/{warehouseId}")
    public ResponseEntity<?> updateWarehouse(@PathVariable Long warehouseId, @RequestBody WarehouseRequestDTO req){
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Update warehouse successful",
                updateWarehouseUseCase.updateWarehouse(warehouseId, req));
    }

    @DeleteMapping("/{warehouseId}")
    public ResponseEntity<?> deleteWarehouseById(@PathVariable Long warehouseId){
        deleteWarehouseUseCase.deleteWarehouseById(warehouseId);
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Delete warehouse successful");
    }
}
