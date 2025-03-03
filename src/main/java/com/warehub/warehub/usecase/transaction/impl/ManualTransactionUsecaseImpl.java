package com.warehub.warehub.usecase.transaction.impl;

import com.warehub.warehub.common.enums.LocationConstants;
import com.warehub.warehub.common.exceptions.*;
import com.warehub.warehub.common.utils.CreateProductMutationLog;
import com.warehub.warehub.common.utils.Location;
import com.warehub.warehub.common.utils.LocationService;
import com.warehub.warehub.entity.*;
import com.warehub.warehub.infrastructure.customerOrderItems.repository.CustomerOrderItemsRepository;
import com.warehub.warehub.infrastructure.customerOrderStatus.CustomerOrderStatusRepository;
import com.warehub.warehub.infrastructure.customerOrders.dto.CustomerOrderResponseDTO;
import com.warehub.warehub.infrastructure.customerOrders.repository.CustomerOrderRepository;
import com.warehub.warehub.infrastructure.paymentMethod.repository.PaymentMethodRepository;
import com.warehub.warehub.infrastructure.product.repository.ProductRepository;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationRepository;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationStatusRepository;
import com.warehub.warehub.infrastructure.productMutation.repository.ProductMutationTypeRepository;
import com.warehub.warehub.infrastructure.transaction.dto.*;
import com.warehub.warehub.infrastructure.users.repository.UsersRepository;
import com.warehub.warehub.infrastructure.warehouse.dto.WarehouseResponseDTO;
import com.warehub.warehub.infrastructure.warehouse.repository.WarehouseRepository;
import com.warehub.warehub.infrastructure.warehouseInventory.repository.WarehouseInventoryRepository;
import com.warehub.warehub.usecase.transaction.ManualTransactionUsecase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ManualTransactionUsecaseImpl implements ManualTransactionUsecase {
    private final CustomerOrderRepository customerOrderRepository;
    private final CustomerOrderItemsRepository customerOrderItemsRepository;
    private final UsersRepository usersRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final CustomerOrderStatusRepository orderStatusRepository;
    private final WarehouseInventoryRepository warehouseInventoryRepository;
    private final ProductMutationStatusRepository productMutationStatusRepository;
    private final ProductMutationTypeRepository productMutationTypeRepository;
    private final ProductMutationRepository productMutationRepository;
    private final CustomerOrderStatusRepository customerOrderStatusRepository;
    private final CreateProductMutationLog createProductMutationLog;

    @Transactional
    @Override
    public ManualTransactionResponseDTO createManualTransaction(ManualTransactionRequestDTO request) {
        // Validate user
        User user = usersRepository.findById(request.getUserId())
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        // Check if the user is verified
        if (!user.getIsEmailVerified()) throw new IllegalArgumentException("User is not verified to perform the action, verify the email first");

        // Generate invoice code
        String invoiceCode = "ORDER-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + UUID.randomUUID().toString().substring(0, 6);

        /*
         * Find nearby warehouse from shipping address
         * */
        Location location = LocationService.validateLocation(request.getLongitude(), request.getLatitude());
        List<WarehouseResponseDTO> nearbyWarehouses = warehouseRepository.findNearestWarehouses(
                location.getLongitude(),
                location.getLatitude(),
                LocationConstants.MAX_DISTANCE_IN_METERS.getValue()
        ).stream().map(obj -> new WarehouseResponseDTO(
                ((Number) obj[0]).longValue(),  // ID
                (String) obj[1]               // Name
        )).toList();

        if (nearbyWarehouses.isEmpty()) throw new DataNotFoundException("No warehouses found nearby");

        // Select the first (nearest) warehouse
        WarehouseResponseDTO nearestWarehouseDTO = nearbyWarehouses.getFirst();

        /*
         * Validate warehouse, Validate payment method and Validate order status
         * */
        Warehouse nearestWarehouse = warehouseRepository.findById(nearestWarehouseDTO.getId())
                .orElseThrow(() -> new DataNotFoundException("Nearest warehouse not found"));
        PaymentMethod paymentMethod = paymentMethodRepository.findById(request.getPaymentMethodId())
                .orElseThrow(() -> new DataNotFoundException("Payment method not found"));
        CustomerOrderStatus orderStatus = orderStatusRepository.findById(request.getOrderStatusId())
                .orElseThrow(() -> new DataNotFoundException("Order status not found"));

        /*
         * Validate the order items is ready stock in the nearest warehouse,
         * or auto-mutate from another warehouse.
         * */
        for (OrderItemDTO item : request.getOrderItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new DataNotFoundException("Product not found"));

            WarehouseInventory inventory  = warehouseInventoryRepository.findByProductIdAndWarehouseIdAndDeletedAtIsNull(
                    item.getProductId(), nearestWarehouse.getId()
            ).orElse(null);

            int requiredQuantity = item.getQuantity();
            int availableQuantity = (inventory != null) ? inventory.getQuantity() : 0;
            int missingQuantity = requiredQuantity - availableQuantity;

            if (missingQuantity > 0) {
                // Search for another warehouse with enough stock
                WarehouseResponseDTO alternateWarehouseDTO = nearbyWarehouses.stream()
                        .skip(1) // Skip the nearest warehouse
                        .map(dto -> warehouseRepository.findById(dto.getId()).orElse(null))
                        .filter(wh -> wh != null && warehouseInventoryRepository.findByProductIdAndWarehouseIdAndDeletedAtIsNull(item.getProductId(), wh.getId())
                                .map(inv -> inv.getQuantity() >= missingQuantity)
                                .orElse(false))
                        .findFirst()
                        .map(wh -> new WarehouseResponseDTO(wh.getId(), wh.getName()))
                        .orElse(null);

                if (alternateWarehouseDTO == null) throw new DataNotFoundException("Not enough stock for " + product.getName() + " across all warehouses");

                Warehouse alternateWarehouse = warehouseRepository.findById(alternateWarehouseDTO.getId())
                        .orElseThrow(() -> new DataNotFoundException("Alternate warehouse not found"));

                WarehouseInventory alternateInventory = warehouseInventoryRepository.findByProductIdAndWarehouseIdAndDeletedAtIsNull(
                        item.getProductId(), alternateWarehouse.getId()
                ).orElseThrow(() -> new DataNotFoundException("Product " + product.getName() + " is out of stock in alternate warehouse"));

                // Deduct stock to nearest warehouse (OUT mutation)
                alternateInventory.setQuantity(alternateInventory.getQuantity() - missingQuantity);
                warehouseInventoryRepository.save(alternateInventory);

                // Add stock to nearest warehouse (IN mutation)
                if (inventory == null) {
                    inventory = new WarehouseInventory();
                    inventory.setWarehouse(nearestWarehouse);
                    inventory.setProduct(product);
                    inventory.setQuantity(0);
                }
                inventory.setQuantity(inventory.getQuantity() + missingQuantity);
                warehouseInventoryRepository.save(inventory);

                // Create product mutation records
                createProductMutationLog.createProductMutationRecord(
                        product, -missingQuantity, "Auto mutation: stock moved to nearest warehouse", user, alternateWarehouse, nearestWarehouse, 2L, 2L, invoiceCode
                );
                createProductMutationLog.createProductMutationRecord(
                        product, missingQuantity, "Auto mutation: stock received from alternate warehouse", user, nearestWarehouse, alternateWarehouse, 2L, 2L, invoiceCode
                );
//                createProductMutationRecord(product, -missingQuantity, "Auto mutation: stock moved to nearest warehouse", user, alternateWarehouse, nearestWarehouse, 2L, 2L, invoiceCode);
//                createProductMutationRecord(product, missingQuantity, "Auto mutation: stock received from alternate warehouse", user, nearestWarehouse, alternateWarehouse, 2L, 2L, invoiceCode);
            }

            // Deduct stock for order
            assert inventory != null;
            inventory.setQuantity(inventory.getQuantity() - requiredQuantity);
            warehouseInventoryRepository.save(inventory);

            // Product auto mutation type
            ProductMutationType productMutationTypeAuto = productMutationTypeRepository.findByIdAndDeletedAtIsNull(2L)
                    .orElseThrow(()-> new ProductMutationTypeNotFoundException("Product mutation type with ID not found !"));

            // Product status type
            ProductMutationStatus productMutationStatusPending = productMutationStatusRepository.findByIdAndDeletedAtIsNull(2L)
                    .orElseThrow(()-> new ProductMutationStatusNotFoundException("Product mutation status with ID not found !"));

            /*
             * Add product mutation record
             * */
            for (OrderItemDTO orderItem : request.getOrderItems()) {
                Product productItem = productRepository.findById(item.getProductId())
                        .orElseThrow(() -> new DataNotFoundException("Product not found"));

                ProductMutation productMutation = new ProductMutation();
                productMutation.setProduct(productItem);
                productMutation.setQuantity(-orderItem.getQuantity()); // Negative to indicate stock decrease
                productMutation.setRequesterNotes("Product sent to customer with payment using manual transfer");
                productMutation.setRequester(user);
                productMutation.setOriginWarehouse(nearestWarehouse);
                productMutation.setProductMutationType(productMutationTypeAuto);
                productMutation.setProductMutationStatus(productMutationStatusPending);
                productMutation.setInvoiceCode(invoiceCode);
                productMutationRepository.save(productMutation);
            }
        }

        /*
         * Create and save customer order
         * */
        CustomerOrder customerOrder = new CustomerOrder();
        customerOrder.setUser(user);
        customerOrder.setWarehouse(nearestWarehouse);
        customerOrder.setPaymentMethod(paymentMethod);
        customerOrder.setShippingCost(request.getShippingCost());
        customerOrder.setTotalAmount(request.getGrossAmount());
        customerOrder.setOrderStatus(orderStatus);
        customerOrder.setInvoiceCode(invoiceCode);
        customerOrder = customerOrderRepository.save(customerOrder);

        /*
         * Create and save customer order items
         * */
        CustomerOrder finalCustomerOrder = customerOrder;
        List<CustomerOrderItem> orderItems = request.getOrderItems().stream().map(item -> {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new DataNotFoundException("Product not found"));

            CustomerOrderItem orderItem = new CustomerOrderItem();
            orderItem.setCustomerOrder(finalCustomerOrder);
            orderItem.setProduct(product);
            orderItem.setQuantity(item.getQuantity());
            orderItem.setProductPrice(item.getUnitPrice());

            return orderItem;
        }).toList();

        customerOrderItemsRepository.saveAll(orderItems);

        // Construct return response
        return new ManualTransactionResponseDTO(
                customerOrder.getId(),
                invoiceCode,
                customerOrder.getTotalAmount(),
                customerOrder.getShippingCost(),
                customerOrder.getPaymentProofImageUrl(),
                user.getId(),
                nearestWarehouse.getId(),
                paymentMethod.getId(),
                Math.toIntExact(orderStatus.getId()),
                request.getOrderItems(),
                customerOrder.getCreatedAt(),
                customerOrder.getUpdatedAt(),
                orderStatus.getName()
        );
    }

    @Override
    public UpdatePaymentProofResponseDTO updateManualPaymentProof(Long customerOrderId, UpdatePaymentProofRequestDTO request) {
        // Validate user
        User user = usersRepository.findById(request.getUserId())
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        // Check if the user is verified
        if (!user.getIsEmailVerified()) {
            throw new IllegalArgumentException("User is not verified to perform the action, verify the email first");
        }

        // Validate customer order exist for the user
        CustomerOrder customerOrder = customerOrderRepository.findByIdAndUserId(customerOrderId, request.getUserId());

        if (customerOrder == null) {
            throw new DataNotFoundException("Customer order with ID " + customerOrderId + " not found for the user.");
        }

        // Check if payment proof already exists
        if (customerOrder.getPaymentProofImageUrl() != null && !customerOrder.getPaymentProofImageUrl().isEmpty()) {
            throw new PaymentProofAlreadyExistsException("Customer order with ID " + customerOrderId + " already has a payment proof image.");
        }

        CustomerOrderStatus waitingForAdminConfirmation = customerOrderStatusRepository.findById(2)
                .orElseThrow(() -> new DataNotFoundException("Customer order with ID " + customerOrderId + " not found"));

        // Update payment proof
        customerOrder.setPaymentProofImageUrl(request.getPaymentProofImage());
        customerOrder.setOrderStatus(waitingForAdminConfirmation);
        customerOrderRepository.save(customerOrder);

        return new UpdatePaymentProofResponseDTO(
                customerOrder.getPaymentProofImageUrl()
        );
    }

    @Override
    public CustomerOrderResponseDTO cancelManualTransaction(Long customerOrderId) {
        // Find the user
//        User user = usersRepository.findById(userId)
//                .orElseThrow(() -> new DataNotFoundException("User not found"));

        // Find the customer order
        CustomerOrder customerOrder = customerOrderRepository.findById(customerOrderId)
                .orElseThrow(() -> new DataNotFoundException("Customer order not found"));

        // Get order items
        List<CustomerOrderItem> orderItems = customerOrderItemsRepository.findByCustomerOrderId(customerOrderId);

        if (orderItems.isEmpty()) throw new DataNotFoundException("No oder items found for this transaction");

        // Reverse stock changes
        for (CustomerOrderItem item : orderItems) {
            Product product = item.getProduct();
            int quantityToReturn = item.getQuantity();

            // Find product mutation records (to track stock movement)
            List<ProductMutation> mutations = productMutationRepository.findByInvoiceCodeAndProductId(
                    customerOrder.getInvoiceCode(), product.getId());

            for (ProductMutation mutation : mutations) {
                Warehouse originWarehouse = mutation.getOriginWarehouse();
                Warehouse destinationWarehouse = mutation.getDestinationWarehouse();
                int mutatedQuantity = mutation.getQuantity(); // The actual quantity moved in mutation

                // Stock was moved out (deducted)
                if (mutatedQuantity < 0) {
                    // Restore stock in the origin warehouse
                    WarehouseInventory originInventory = warehouseInventoryRepository
                            .findByProductIdAndWarehouseIdAndDeletedAtIsNull(product.getId(), originWarehouse.getId())
                            .orElseThrow(() -> new DataNotFoundException("Inventory record not found"));

                    // Restore only what is needed
                    int quantityToRestore = Math.min(quantityToReturn, Math.abs(mutatedQuantity));
                    originInventory.setQuantity(originInventory.getQuantity() + quantityToRestore);
                    warehouseInventoryRepository.save(originInventory);

                    // Log mutation (reverse stock movement)
                    createProductMutationLog.createProductMutationRecord(
                            product, quantityToRestore, "Order canceled : reversing transaction calcellation",
                            customerOrder.getUser(), destinationWarehouse, originWarehouse,
                            2L, 3L, customerOrder.getInvoiceCode()
                    );

                    // Reduce remaining quantity to return
                    quantityToReturn -= quantityToRestore;
                    if (quantityToReturn <= 0) break; // Stop if all stock is restored
                }
            }
        }
        // Mark order as canceled
        CustomerOrderStatus canceledStatus = customerOrderStatusRepository.findById(6)
                .orElseThrow(() -> new DataNotFoundException("Order status not found"));
        customerOrder.setOrderStatus(canceledStatus);
        customerOrderRepository.save(customerOrder);

        return CustomerOrderResponseDTO.mapToDTO(customerOrder);
    }
}