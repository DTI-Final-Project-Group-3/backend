package com.warehub.warehub.infrastructure.warehouse_inventories.controller;

import com.warehub.warehub.common.response.ApiResponse;
import com.warehub.warehub.infrastructure.warehouse_inventories.dto.WarehouseInventoryRequestDTO;
import com.warehub.warehub.usecase.warehouse_inventories.CreateWarehouseInventoryUseCase;
import com.warehub.warehub.usecase.warehouse_inventories.DeleteWarehouseInventoryUseCase;
import com.warehub.warehub.usecase.warehouse_inventories.GetWarehouseInventoryUseCase;
import com.warehub.warehub.usecase.warehouse_inventories.UpdateWarehouseInventoryUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/warehouse-inventories")
public class WarehouseInventoryController {

    private final CreateWarehouseInventoryUseCase createWarehouseInventoryUseCase;
    private final GetWarehouseInventoryUseCase getWarehouseInventoryUseCase;
    private final UpdateWarehouseInventoryUseCase updateWarehouseInventoryUseCase;
    private final DeleteWarehouseInventoryUseCase deleteWarehouseInventoryUseCase;

    public WarehouseInventoryController(CreateWarehouseInventoryUseCase createWarehouseInventoryUseCase, GetWarehouseInventoryUseCase getWarehouseInventoryUseCase, UpdateWarehouseInventoryUseCase updateWarehouseInventoryUseCase, DeleteWarehouseInventoryUseCase deleteWarehouseInventoryUseCase) {
        this.createWarehouseInventoryUseCase = createWarehouseInventoryUseCase;
        this.getWarehouseInventoryUseCase = getWarehouseInventoryUseCase;
        this.updateWarehouseInventoryUseCase = updateWarehouseInventoryUseCase;
        this.deleteWarehouseInventoryUseCase = deleteWarehouseInventoryUseCase;
    }

    @PostMapping
    public ResponseEntity<?> createWarehouseInventory(@RequestBody WarehouseInventoryRequestDTO req){
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Create new warehouse inventory success", createWarehouseInventoryUseCase.createWarehouseInventory(req));
    }

    @GetMapping("/{warehouseInventoryId}")
    public ResponseEntity<?> getWarehouseInventoryById(@PathVariable Long warehouseInventoryId){
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Get warehouse inventory success", getWarehouseInventoryUseCase.getWarehouseInventoryById(warehouseInventoryId));
    }

    @GetMapping("/warehouses/{warehouseId}")
    public ResponseEntity<?> getWarehouseInventoryByWarehouseId(@PathVariable Long warehouseId){
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Get warehouse inventory success", getWarehouseInventoryUseCase.getWarehouseInventoryByWarehouseId(warehouseId));
    }

    @PutMapping("/{warehouseInventoryId}")
    public ResponseEntity<?> updateWarehouseInventoryById(@PathVariable Long warehouseInventoryId,
                                                          @RequestBody WarehouseInventoryRequestDTO req){
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Update warehouse inventory success", updateWarehouseInventoryUseCase.updateQuantity(warehouseInventoryId, req));
    }

    @DeleteMapping("/{warehouseInventoryId}")
    public ResponseEntity<?> deleteWarehouseInventoryId(@PathVariable Long warehouseInventoryId){
        deleteWarehouseInventoryUseCase.deletedWarehouseInventoryById(warehouseInventoryId);
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Delete warehouse inventory success");
    }
}
