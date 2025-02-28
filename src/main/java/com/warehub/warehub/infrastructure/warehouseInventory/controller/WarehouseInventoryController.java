package com.warehub.warehub.infrastructure.warehouseInventory.controller;

import com.warehub.warehub.common.response.ApiResponse;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationProcessRequestDTO;
import com.warehub.warehub.infrastructure.productMutation.dto.ProductMutationRequestDTO;
import com.warehub.warehub.infrastructure.warehouseInventory.dto.WarehouseInventoryPaginationRequestDTO;
import com.warehub.warehub.usecase.warehouseInventory.CreateWarehouseInventoryUseCase;
import com.warehub.warehub.usecase.warehouseInventory.DeleteWarehouseInventoryUseCase;
import com.warehub.warehub.usecase.warehouseInventory.GetWarehouseInventoryUseCase;
import com.warehub.warehub.usecase.warehouseInventory.UpdateWarehouseInventoryUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/warehouses/inventories")
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
    public ResponseEntity<?> createWarehouseInventory(@RequestBody ProductMutationRequestDTO req){
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Create new warehouse inventory success", createWarehouseInventoryUseCase.createWarehouseInventory(req));
    }

    @GetMapping
    public ResponseEntity<?> getPaginatedWarehouseInventoryByWarehouseId(@RequestParam("page") int page,
                                                                         @RequestParam("limit") int limit,
                                                                         @RequestParam("warehouseId") Long warehouseId,
                                                                         @RequestParam(value = "searchParam", required = false) String searchParam){
        WarehouseInventoryPaginationRequestDTO requestDTO = new WarehouseInventoryPaginationRequestDTO(page, limit, warehouseId, searchParam);
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Get warehouse inventory by warehouse id success", getWarehouseInventoryUseCase.getPaginatedWarehouseInventoryByWarehouseId(requestDTO));
    }

    @PutMapping("/quantity/{warehouseInventoryId}")
    public ResponseEntity<?> updateWarehouseInventoryById(@PathVariable Long warehouseInventoryId,
                                                          @RequestBody ProductMutationRequestDTO req){
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Update warehouse inventory quantity success", updateWarehouseInventoryUseCase.updateQuantity(warehouseInventoryId, req));
    }

    @DeleteMapping("/{warehouseInventoryId}")
    public ResponseEntity<?> deleteWarehouseInventoryId(@PathVariable Long warehouseInventoryId, @RequestBody ProductMutationProcessRequestDTO req){

        deleteWarehouseInventoryUseCase.deletedWarehouseInventoryById(warehouseInventoryId, req);
        return ApiResponse.successfulResponse(HttpStatus.OK.value(), "Delete warehouse inventory success");
    }
}
