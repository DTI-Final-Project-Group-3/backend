package com.warehub.warehub.infrastructure.productMutation.scheduler;

import com.warehub.warehub.common.enums.MutationConstant;
import com.warehub.warehub.common.exceptions.ProductMutationStatusNotFoundException;
import com.warehub.warehub.common.exceptions.WarehouseInventoryNotFoundException;
import com.warehub.warehub.common.utils.ValidationService;
import com.warehub.warehub.entity.ProductMutation;
import com.warehub.warehub.entity.ProductMutationStatus;
import com.warehub.warehub.entity.WarehouseInventory;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationRepository;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationStatusRepository;
import com.warehub.warehub.infrastructure.warehouseInventory.repository.WarehouseInventoryRepository;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@EnableScheduling
public class ProductMutationExpiryService {

    private final ValidationService validationService;
    private final ProductMutationRepository productMutationRepository;
    private final WarehouseInventoryRepository warehouseInventoryRepository;

    public ProductMutationExpiryService(ValidationService validationService, ProductMutationRepository productMutationRepository, WarehouseInventoryRepository warehouseInventoryRepository) {
        this.validationService = validationService;
        this.productMutationRepository = productMutationRepository;
        this.warehouseInventoryRepository = warehouseInventoryRepository;
    }

    @Scheduled(fixedDelay = 300000)
    @Transactional
    public void processExpiredMutations(){

        OffsetDateTime now = OffsetDateTime.now();
        String expiryInterval = "1 MINUTE";

        List<ProductMutation> expiredMutations = productMutationRepository.findPendingExpired(now, expiryInterval);

        ProductMutationStatus expiredStatus = validationService.validateProductMutationStatusId(MutationConstant.STATUS_EXPIRED.getValue());

        for (ProductMutation mutation : expiredMutations){

            WarehouseInventory inventory = validationService.validateWarehouseInventoryByProductIdAndWarehouseId(mutation.getProduct().getId(), mutation.getOriginWarehouse().getId());

            // restore quantity
            inventory.setQuantity(inventory.getQuantity() + mutation.getQuantity());
            warehouseInventoryRepository.save(inventory);

            // update mutation status
            mutation.setProductMutationStatus(expiredStatus);
            mutation.setUpdatedAt(now);
            productMutationRepository.save(mutation);
        }

    }
}
