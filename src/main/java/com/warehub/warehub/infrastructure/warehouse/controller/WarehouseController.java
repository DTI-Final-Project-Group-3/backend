package com.warehub.warehub.infrastructure.warehouse.controller;

import com.warehub.warehub.common.response.ApiResponse;
import com.warehub.warehub.infrastructure.warehouse.dto.WarehouseRequestDTO;
import com.warehub.warehub.usecase.warehouse.CreateWarehouseUseCase;
import com.warehub.warehub.usecase.warehouse.DeleteWarehouseUseCase;
import com.warehub.warehub.usecase.warehouse.GetWarehouseUseCase;
import com.warehub.warehub.usecase.warehouse.UpdateWarehouseUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/warehouse")
public class WarehouseController {

    private final CreateWarehouseUseCase createWarehouseUseCase;
    private final GetWarehouseUseCase getWarehouseUseCase;
    private final UpdateWarehouseUseCase updateWarehouseUseCase;
    private final DeleteWarehouseUseCase deleteWarehouseUseCase;

    public WarehouseController(CreateWarehouseUseCase createWarehouseUseCase, GetWarehouseUseCase getWarehouseUseCase, UpdateWarehouseUseCase updateWarehouseUseCase, DeleteWarehouseUseCase deleteWarehouseUseCase) {
        this.createWarehouseUseCase = createWarehouseUseCase;
        this.getWarehouseUseCase = getWarehouseUseCase;
        this.updateWarehouseUseCase = updateWarehouseUseCase;
        this.deleteWarehouseUseCase = deleteWarehouseUseCase;
    }

    @GetMapping("/{warehouseId}")
    public ResponseEntity<?> getWarehouseById(@PathVariable Long warehouseId){
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Get warehouse by ID success", getWarehouseUseCase.getWarehouseById(warehouseId));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllWarehouse(){
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Get all warehouse success", getWarehouseUseCase.getAllWarehouse());
    }

    @GetMapping("/nearby")
    public ResponseEntity<?> getNearbyWarehouses(@RequestParam double lng,
                                                 @RequestParam double lat,
                                                 @RequestParam double max){
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Get nearby warehouse success", getWarehouseUseCase.getNearbyWarehouses(lng, lat, max));
    }

    @PostMapping
    public ResponseEntity<?> createWarehouse(@RequestBody WarehouseRequestDTO req){
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Create warehouse success", createWarehouseUseCase.createWarehouse(req));
    }

    @PutMapping
    public ResponseEntity<?> updateWarehouse(@RequestBody WarehouseRequestDTO req){
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Update warehouse successful", updateWarehouseUseCase.updateWarehouse(req));
    }

    @DeleteMapping("/{warehouseId}")
    public ResponseEntity<?> deleteWarehouseById(@PathVariable Long warehouseId){
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Delete warehouse successful",deleteWarehouseUseCase.deleteWarehouseById(warehouseId));
    }
}
