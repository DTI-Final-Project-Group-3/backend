package com.warehub.warehub.usecase.transaction.impl;

import com.warehub.warehub.common.enums.LocationConstants;
import com.warehub.warehub.common.exceptions.*;
import com.warehub.warehub.common.utils.Location;
import com.warehub.warehub.common.utils.LocationService;
import com.warehub.warehub.entity.*;
import com.warehub.warehub.infrastructure.customerOrderItems.repository.CustomerOrderItemsRepository;
import com.warehub.warehub.infrastructure.customerOrderStatus.CustomerOrderStatusRepository;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
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

    public ManualTransactionUsecaseImpl(
            CustomerOrderRepository customerOrderRepository,
            CustomerOrderItemsRepository customerOrderItemsRepository,
            UsersRepository usersRepository,
            WarehouseRepository warehouseRepository,
            ProductRepository productRepository,
            PaymentMethodRepository paymentMethodRepository,
            CustomerOrderStatusRepository orderStatusRepository, WarehouseInventoryRepository warehouseInventoryRepository, ProductMutationStatusRepository productMutationStatusRepository, ProductMutationTypeRepository productMutationTypeRepository, ProductMutationRepository productMutationRepository)
    {
        this.customerOrderRepository = customerOrderRepository;
        this.customerOrderItemsRepository = customerOrderItemsRepository;
        this.usersRepository = usersRepository;
        this.warehouseRepository = warehouseRepository;
        this.productRepository = productRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.orderStatusRepository = orderStatusRepository;
        this.warehouseInventoryRepository = warehouseInventoryRepository;
        this.productMutationStatusRepository = productMutationStatusRepository;
        this.productMutationTypeRepository = productMutationTypeRepository;
        this.productMutationRepository = productMutationRepository;
    }

    @Transactional
    @Override
    public ManualTransactionResponseDTO createManualTransaction(ManualTransactionRequestDTO request) {
        // Validate user
        User user = usersRepository.findById(request.getUserId())
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        // Check if the user is verified
        if (!user.getIsEmailVerified()) {
            throw new IllegalArgumentException("User is not verified to perform the action, verify the email first");
        }

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
                int updateQuantity = inventory.getQuantity() + missingQuantity;
                Long status = (updateQuantity == 0) ? 2L : 1L;

                inventory.setQuantity(inventory.getQuantity() + missingQuantity);
                warehouseInventoryRepository.save(inventory);

                // Create product mutation records
                createProductMutationRecord(product, -missingQuantity, "Auto mutation: stock moved to nearest warehouse", user, alternateWarehouse, nearestWarehouse, 2L, 2L);
                createProductMutationRecord(product, missingQuantity, "Auto mutation: stock received from alternate warehouse", user, nearestWarehouse, alternateWarehouse, 2L, 2L);
            }

            // Deduct stock for order
            assert inventory != null;
            int updateQuantity = inventory.getQuantity() - requiredQuantity;
            Long status = (updateQuantity == 0) ? 2L : 1L;

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
                productMutation.setRequester(user);
                productMutation.setOriginWarehouse(nearestWarehouse);
                productMutation.setProductMutationType(productMutationTypeAuto);
                productMutation.setProductMutationStatus(productMutationStatusPending);
//            productMutation.setAcceptedAt(OffsetDateTime.now());
                productMutationRepository.save(productMutation);
            }
        }

        // Generate invoice code
        String invoiceCode = "ORDER-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + UUID.randomUUID().toString().substring(0, 6);

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

        if (customerOrder.getPaymentProofImageUrl().isEmpty()) {
            customerOrder.setPaymentProofImageUrl(request.getPaymentProofImage());
        } else throw new PaymentProofAlreadyExistsException("Customer order with Id " + customerOrderId + " already has a payment proof image");
        customerOrderRepository.save(customerOrder);

        return new UpdatePaymentProofResponseDTO(
                customerOrder.getPaymentProofImageUrl()
        );
    }

    /**
     * Helper method to create a product mutation record.
     */
    public void createProductMutationRecord(Product product, int quantity, String notes, User user,
                                            Warehouse fromWarehouse, Warehouse toWarehouse, Long mutationTypeId, Long mutationStatusId) {

        ProductMutationType mutationType = productMutationTypeRepository.findByIdAndDeletedAtIsNull(mutationTypeId)
                .orElseThrow(() -> new ProductMutationTypeNotFoundException("Product mutation type not found"));

        ProductMutationStatus mutationStatus = productMutationStatusRepository.findByIdAndDeletedAtIsNull(mutationStatusId)
                .orElseThrow(() -> new ProductMutationStatusNotFoundException("Product mutation status not found"));

        ProductMutation mutation = new ProductMutation();
        mutation.setProduct(product);
        mutation.setQuantity(quantity);
        mutation.setRequester(user);
        mutation.setOriginWarehouse(fromWarehouse);
        mutation.setDestinationWarehouse(toWarehouse);
        mutation.setProductMutationType(mutationType);
        mutation.setProductMutationStatus(mutationStatus);
        productMutationRepository.save(mutation);
    }
}

