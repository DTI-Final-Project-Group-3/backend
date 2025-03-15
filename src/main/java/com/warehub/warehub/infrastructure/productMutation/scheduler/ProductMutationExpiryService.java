package com.warehub.warehub.infrastructure.productMutation.scheduler;

import com.warehub.warehub.common.enums.MutationConstant;
import com.warehub.warehub.common.utils.ValidationService;
import com.warehub.warehub.entity.ProductMutation;
import com.warehub.warehub.entity.ProductMutationStatus;
import com.warehub.warehub.entity.WarehouseInventory;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationRepository;
import com.warehub.warehub.infrastructure.warehouseInventory.repository.WarehouseInventoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ProductMutationExpiryService {

    private final ValidationService validationService;
    private final ProductMutationRepository productMutationRepository;
    private final WarehouseInventoryRepository warehouseInventoryRepository;

    public ProductMutationExpiryService(ValidationService validationService, ProductMutationRepository productMutationRepository, WarehouseInventoryRepository warehouseInventoryRepository) {
        this.validationService = validationService;
        this.productMutationRepository = productMutationRepository;
        this.warehouseInventoryRepository = warehouseInventoryRepository;
    }

    @Scheduled(cron = "0 */2 * * * *") // every 2 minutes
    @Transactional
    public void processExpiredMutations(){
        System.out.println("Scheduled : Started CRON JOB for expired product mutation");

        List<ProductMutation> expiredMutations = productMutationRepository.findPendingExpired();

        for (ProductMutation expiredMutation : expiredMutations){

            // restore quantity on outbound mutation
            if (expiredMutation.getProductMutationType().getId().equals(MutationConstant.TYPE_OUTBOUND_MANUAL_MUTATION.getValue())) {
                WarehouseInventory inventory = validationService.validateWarehouseInventoryByProductIdAndWarehouseId(
                        expiredMutation.getProduct().getId(),
                        expiredMutation.getOriginWarehouse().getId()
                );
                inventory.setQuantity(inventory.getQuantity() + -expiredMutation.getQuantity());
                warehouseInventoryRepository.save(inventory);
            }

            // update status
            ProductMutationStatus statusExpired = validationService.validateProductMutationStatusId(MutationConstant.STATUS_EXPIRED.getValue());
            expiredMutation.setProductMutationStatus(statusExpired);
            productMutationRepository.save(expiredMutation);
        }

        System.out.println("Ended CRON JOB for expired product mutation");
    }
}
