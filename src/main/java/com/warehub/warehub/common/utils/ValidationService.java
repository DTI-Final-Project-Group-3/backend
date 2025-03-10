package com.warehub.warehub.common.utils;

import com.warehub.warehub.common.exceptions.*;
import com.warehub.warehub.entity.*;
import com.warehub.warehub.infrastructure.product.repository.ProductCategoryRepository;
import com.warehub.warehub.infrastructure.product.repository.ProductRepository;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationRepository;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationStatusRepository;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationTypeRepository;
import com.warehub.warehub.infrastructure.users.repository.UsersRepository;
import com.warehub.warehub.infrastructure.warehouse.repository.WarehouseRepository;
import com.warehub.warehub.infrastructure.warehouseInventory.repository.WarehouseInventoryRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class ValidationService {

    private final UsersRepository usersRepository;
    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductMutationRepository productMutationRepository;
    private final ProductMutationTypeRepository productMutationTypeRepository;
    private final ProductMutationStatusRepository productMutationStatusRepository;
    private final WarehouseRepository warehouseRepository;
    private final WarehouseInventoryRepository warehouseInventoryRepository;

    public ValidationService(UsersRepository usersRepository, ProductRepository productRepository, ProductCategoryRepository productCategoryRepository, ProductMutationRepository productMutationRepository, ProductMutationTypeRepository productMutationTypeRepository, ProductMutationStatusRepository productMutationStatusRepository, WarehouseRepository warehouseRepository, WarehouseInventoryRepository warehouseInventoryRepository) {
        this.usersRepository = usersRepository;
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.productMutationRepository = productMutationRepository;
        this.productMutationTypeRepository = productMutationTypeRepository;
        this.productMutationStatusRepository = productMutationStatusRepository;
        this.warehouseRepository = warehouseRepository;
        this.warehouseInventoryRepository = warehouseInventoryRepository;
    }

    public void validateDateRange(LocalDate startedAt, LocalDate endedAt){

        if (startedAt == null && endedAt == null) return;

        if (startedAt != null && endedAt != null) {
            if (startedAt.isAfter(endedAt)) {
                throw new DateTimeException("Start date cannot be after end date!");
            }
        } else {
            throw new DateTimeException("Both start date and end date must be provided!");
        }
    }

    public User validateUserId(Long userId){
        if (userId != null){
            return usersRepository.findByIdAndDeletedAtIsNull(userId)
                    .orElseThrow(()-> new UsernameNotFoundException("User with Id " + userId + " not found !"));
        }
        return null;
    }

    public Product validateProductId(Long productId){
        if (productId != null){
            return productRepository.findByIdAndDeletedAtIsNull(productId)
                    .orElseThrow(()-> new ProductNotFoundException("Product with Id " + productId + " not found !"));
        }
        return  null;
    }

    public void validateDuplicateProductName(String productName){
        Optional<Product> duplicateProduct = productRepository.findByNameIgnoreCaseAndDeletedAtIsNull(productName);
        if (duplicateProduct.isPresent()){
            throw new DuplicateProductException("Product with name "+ productName + " already exist !");
        }
    }

    public ProductCategory validateProductCategoryId(Long productCategoryId){
        if (productCategoryId != null){
            return productCategoryRepository.findByIdAndDeletedAtIsNull(productCategoryId)
                    .orElseThrow(()-> new ProductCategoryNotFoundException("Product category with Id " + productCategoryId + " not found !"));

        }
        return null;
    }

    public void validateDuplicateProductCategoryName(String productCategoryName){
        Optional<ProductCategory> productCategory = productCategoryRepository.findByNameIgnoreCaseAndDeletedAtIsNull(productCategoryName);

        if (productCategory.isPresent()){
            throw new DuplicateProductCategoryException("Product category with name " + productCategoryName + " already exist !");
        }
    }

    public ProductMutation validateProductMutationId(Long productMutationId){
        if (productMutationId != null){
            return productMutationRepository.findByIdAndDeletedAtIsNull(productMutationId)
                    .orElseThrow(()-> new ProductMutationNotFoundException("Product mutation with Id "+ productMutationId + " no found !"));
        }
        return null;
    }

    public ProductMutationType validateProductMutationTypeId(Long productMutationTypeId){
        if (productMutationTypeId != null){
            return  productMutationTypeRepository.findByIdAndDeletedAtIsNull(productMutationTypeId)
                    .orElseThrow(()-> new ProductMutationTypeNotFoundException("Product mutation type with Id " + productMutationTypeId + " not found !"));
        }
        return null;
    }

    public ProductMutationStatus validateProductMutationStatusId(Long productMutationStatusId){
        if (productMutationStatusId != null){
            return productMutationStatusRepository.findByIdAndDeletedAtIsNull(productMutationStatusId)
                    .orElseThrow(()-> new ProductMutationStatusNotFoundException("Product mutation status with Id " + productMutationStatusId + " not found !"));
        }
        return null;
    }

    public Warehouse validateWarehouseId(Long warehouseId, String desc){
        if (warehouseId != null){
            return warehouseRepository.findByIdAndDeletedAtIsNull(warehouseId)
                    .orElseThrow(()-> new WarehouseNotFoundException( desc + "with Id "+ warehouseId + " not found !"));
        }
        return null;
    }

    public void validateDuplicateWarehouseName(String warehouseName){
        Optional<Warehouse> warehouse = warehouseRepository.findByNameIgnoreCaseAndDeletedAtIsNull(warehouseName);
        if (warehouse.isPresent()){
            throw new DuplicateWarehouseException("Warehouse with name " + warehouseName + " already exist !");
        }
    }

    public WarehouseInventory validateWarehouseInventoryId(Long warehouseInventoryId){
        if (warehouseInventoryId != null){
            return warehouseInventoryRepository.findByIdAndDeletedAtIsNull(warehouseInventoryId)
                    .orElseThrow(()-> new WarehouseNotFoundException("Warehouse inventory with Id "+ warehouseInventoryId + " not found !"));
        }
        return null;
    }

    public WarehouseInventory validateWarehouseInventoryByProductIdAndWarehouseId(Long productId, Long warehouseId){
        if (productId != null && warehouseId != null){
            return warehouseInventoryRepository.findByProductIdAndWarehouseIdAndDeletedAtIsNull(productId, warehouseId)
                    .orElseThrow(()-> new WarehouseInventoryNotFoundException("Warehouse inventory for product Id " + productId + " and warehouse Id " + warehouseId + " not found !"));
        }
        return null;
    }

    public void validateDuplicateWarehouseInventory(Long productId, Long warehouseId){
        boolean warehouseInventoryExist = warehouseInventoryRepository.existsByProductIdAndWarehouseIdAndDeletedAtIsNull(productId, warehouseId);
        if (warehouseInventoryExist){
            throw new DuplicateWarehouseInventoryException("Warehouse Inventory with product ID " + productId + " and warehouse ID " + warehouseId + " already exist !");
        }
    }

    public void validateMaximumSize(int size, int limit, String desc){
        if (size > limit){
            throw new MaxListSizeExceededException("Maximum number of " + desc + " exceeded !");
        }
    }
}
