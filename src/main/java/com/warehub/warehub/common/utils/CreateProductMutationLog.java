package com.warehub.warehub.common.utils;

import com.warehub.warehub.common.exceptions.ProductMutationStatusNotFoundException;
import com.warehub.warehub.common.exceptions.ProductMutationTypeNotFoundException;
import com.warehub.warehub.entity.*;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationRepository;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationStatusRepository;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateProductMutationLog {

    private final ProductMutationRepository productMutationRepository;
    private final ProductMutationTypeRepository productMutationTypeRepository;
    private final ProductMutationStatusRepository productMutationStatusRepository;

    /**
     * Creates a product mutation record.
     *
     * @param product         The product being mutated.
     * @param quantity        The quantity of the product.
     * @param notes           Notes related to the mutation.
     * @param user            The user who initiated the mutation.
     * @param fromWarehouse   The warehouse from which the product is moved.
     * @param toWarehouse     The warehouse to which the product is moved.
     * @param mutationTypeId  The type of mutation.
     * @param mutationStatusId The status of the mutation.
     * @param invoiceCode     The invoice code for tracking.
     */
    public void createProductMutationRecord(
            Product product, int quantity, String notes, User user,
            Warehouse fromWarehouse, Warehouse toWarehouse,
            Long mutationTypeId, Long mutationStatusId, String invoiceCode) {

        ProductMutationType mutationType = productMutationTypeRepository.findByIdAndDeletedAtIsNull(mutationTypeId)
                .orElseThrow(() -> new ProductMutationTypeNotFoundException("Product mutation type not found"));

        ProductMutationStatus mutationStatus = productMutationStatusRepository.findByIdAndDeletedAtIsNull(mutationStatusId)
                .orElseThrow(() -> new ProductMutationStatusNotFoundException("Product mutation status not found"));

        ProductMutation mutation = new ProductMutation();
        mutation.setProduct(product);
        mutation.setQuantity(quantity);
        mutation.setRequesterNotes(notes);
        mutation.setRequester(user);
        mutation.setOriginWarehouse(fromWarehouse);
        mutation.setDestinationWarehouse(toWarehouse);
        mutation.setProductMutationType(mutationType);
        mutation.setProductMutationStatus(mutationStatus);
        mutation.setInvoiceCode(invoiceCode);

        productMutationRepository.save(mutation);
    }
}
