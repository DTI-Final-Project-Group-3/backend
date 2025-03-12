package com.warehub.warehub.infrastructure.productMutation.scheduler;

import com.warehub.warehub.common.enums.MutationConstant;
import com.warehub.warehub.common.utils.CreateProductMutationLog;
import com.warehub.warehub.common.utils.ValidationService;
import com.warehub.warehub.entity.ProductMutation;
import com.warehub.warehub.entity.WarehouseInventory;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationRepository;
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
    private final CreateProductMutationLog createProductMutationLog;

    public ProductMutationExpiryService(ValidationService validationService, ProductMutationRepository productMutationRepository, WarehouseInventoryRepository warehouseInventoryRepository, CreateProductMutationLog createProductMutationLog) {
        this.validationService = validationService;
        this.productMutationRepository = productMutationRepository;
        this.warehouseInventoryRepository = warehouseInventoryRepository;
        this.createProductMutationLog = createProductMutationLog;
    }

    @Scheduled(fixedDelay = 120000)
    @Transactional
    public void processExpiredMutations(){
        System.out.println("Started CRON JOB for expired product mutation");

        OffsetDateTime now = OffsetDateTime.now();
        String expiryInterval = "1 MINUTE";

        List<ProductMutation> expiredMutations = productMutationRepository.findPendingExpired(now, expiryInterval);

        for (ProductMutation mutation : expiredMutations){

            WarehouseInventory inventory = validationService.validateWarehouseInventoryByProductIdAndWarehouseId(mutation.getProduct().getId(), mutation.getOriginWarehouse().getId());

            // restore quantity
            inventory.setQuantity(inventory.getQuantity() + mutation.getQuantity());
            warehouseInventoryRepository.save(inventory);

            // update prev mutation
            mutation.setReviewedAt(OffsetDateTime.now());
            productMutationRepository.save(mutation);


            // create inbound mutation on origin warehouse to restore stock
            createProductMutationLog
                    .createProductMutationRecord(mutation.getProduct(), mutation.getQuantity(),
                            mutation.getRequesterNotes(), mutation.getRequester(),
                            mutation.getOriginWarehouse(), mutation.getDestinationWarehouse(),
                            mutation.getProductMutationType().getId(), MutationConstant.STATUS_EXPIRED.getValue(),  null,
                            "Expired Manual Mutation", null, OffsetDateTime.now(), mutation.getProductMutationCode());
        }
        System.out.println("Ended CRON JOB for expired product mutation");

    }
}
