package com.warehub.warehub.usecase.transaction.impl;

import com.midtrans.Midtrans;
import com.midtrans.httpclient.SnapApi;
import com.midtrans.httpclient.error.MidtransError;
import com.warehub.warehub.common.enums.LocationConstants;
import com.warehub.warehub.common.exceptions.DataNotFoundException;
import com.warehub.warehub.common.exceptions.ProductMutationStatusNotFoundException;
import com.warehub.warehub.common.exceptions.ProductMutationTypeNotFoundException;
import com.warehub.warehub.common.utils.CreateProductMutationLog;
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
import com.warehub.warehub.infrastructure.transaction.dto.OrderItemDTO;
import com.warehub.warehub.infrastructure.transaction.dto.GatewayTransactionRequestDTO;
import com.warehub.warehub.infrastructure.transaction.dto.GatewayTransactionResponseDTO;
import com.warehub.warehub.infrastructure.users.repository.UsersRepository;
import com.warehub.warehub.infrastructure.warehouse.dto.WarehouseResponseDTO;
import com.warehub.warehub.infrastructure.warehouse.repository.WarehouseRepository;
import com.warehub.warehub.infrastructure.warehouseInventory.repository.WarehouseInventoryRepository;
import com.warehub.warehub.usecase.transaction.GatewayTransactionUsecase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GatewayTransactionUsecaseImpl implements GatewayTransactionUsecase {
    private final CustomerOrderRepository customerOrderRepository;
    private final CustomerOrderItemsRepository customerOrderItemsRepository;
    private final UsersRepository usersRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final CustomerOrderStatusRepository orderStatusRepository;
    private final WarehouseInventoryRepository warehouseInventoryRepository;
    private final ProductMutationTypeRepository productMutationTypeRepository;
    private final ProductMutationStatusRepository productMutationStatusRepository;
    private final ProductMutationRepository productMutationRepository;

    // Set serverKey to Midtrans global config
    static {
        Midtrans.serverKey =  System.getenv("MIDTRANS_SERVER_KEY");
        Midtrans.isProduction = false;
    }

    private final CreateProductMutationLog createProductMutationLog;

    @Transactional
    @Override
    public GatewayTransactionResponseDTO createGatewayTransaction(GatewayTransactionRequestDTO trxRequest) {
        try {
            // Validate user
            User user = usersRepository.findById(trxRequest.getUserId())
                    .orElseThrow(() -> new DataNotFoundException("User not found"));

            // Check if the user is verified
            if (!user.getIsEmailVerified()) throw new IllegalArgumentException("User is not verified to perform the action, verified the email first");

            // Create params JSON Raw Object request
            Map<String, Object> params = new HashMap<>();
            // Generate invoice code
            String invoiceCode = "ORDER-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + UUID.randomUUID().toString().substring(0, 6);

            /*
             * Find nearby warehouse from shipping address
             * */
            Location location = LocationService.validateLocation(trxRequest.getLongitude(), trxRequest.getLatitude());
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
            PaymentMethod paymentMethod = paymentMethodRepository.findById(trxRequest.getPaymentMethodId())
                    .orElseThrow(() -> new DataNotFoundException("Payment method not found"));
            CustomerOrderStatus orderStatus = orderStatusRepository.findById(trxRequest.getOrderStatusId())
                    .orElseThrow(() -> new DataNotFoundException("Order status not found"));

            /*
             * Validate the order items is ready stock in the nearest warehouse,
             * or auto-mutate from another warehouse.
             * */
            for (OrderItemDTO item : trxRequest.getOrderItems()) {
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
                }

                // Deduct stock for order
                assert inventory != null;
                inventory.setQuantity(inventory.getQuantity() - requiredQuantity);
                warehouseInventoryRepository.save(inventory);


            }
            
            // Product auto mutation type
            ProductMutationType productMutationTypeAuto = productMutationTypeRepository.findByIdAndDeletedAtIsNull(2L)
                    .orElseThrow(()-> new ProductMutationTypeNotFoundException("Product mutation type with ID not found !"));

            // Product status type
            ProductMutationStatus productMutationStatusPending = productMutationStatusRepository.findByIdAndDeletedAtIsNull(2L)
                    .orElseThrow(()-> new ProductMutationStatusNotFoundException("Product mutation status with ID not found !"));

            /*
             * Add product mutation record
             * */
            for (OrderItemDTO orderItem : trxRequest.getOrderItems()) {
                Product productItem = productRepository.findById(orderItem.getProductId())
                        .orElseThrow(() -> new DataNotFoundException("Product not found"));

                ProductMutation productMutation = new ProductMutation();
                productMutation.setProduct(productItem);
                productMutation.setQuantity(-orderItem.getQuantity()); // Negative to indicate stock decrease
                productMutation.setRequesterNotes("Product sent to customer with payment using Midtrans transfer");
                productMutation.setRequester(user);
                productMutation.setOriginWarehouse(nearestWarehouse);
                productMutation.setProductMutationType(productMutationTypeAuto);
                productMutation.setProductMutationStatus(productMutationStatusPending);
                productMutation.setInvoiceCode(invoiceCode);
                productMutationRepository.save(productMutation);
            }

            /*
             * Prepare transaction params for Midtrans
             * */
            long roundedGrossAmount = trxRequest.getGrossAmount()
                    .setScale(0, RoundingMode.HALF_UP).longValue();

            Map<String, Object> transactionDetails = new HashMap<>();
            transactionDetails.put("order_id", invoiceCode);
            transactionDetails.put("gross_amount", roundedGrossAmount);
            params.put("transaction_details", transactionDetails);

            // Midtrans transaction
            String token = SnapApi.createTransactionToken(params);
            String redirectUrl = System.getenv("MIDTRANS_API_URL") + token;

            /*
             * Create and save customer order
             * */
            CustomerOrder customerOrder = new CustomerOrder();
            customerOrder.setUser(user);
            customerOrder.setWarehouse(nearestWarehouse);
            customerOrder.setPaymentMethod(paymentMethod);
            customerOrder.setShippingCost(trxRequest.getShippingCost());
            customerOrder.setTotalAmount(trxRequest.getGrossAmount());
            customerOrder.setGatewayTrxId(token);
            customerOrder.setOrderStatus(orderStatus);
            customerOrder.setInvoiceCode(invoiceCode);
            customerOrder = customerOrderRepository.save(customerOrder);

            /*
             * Create and save customer order items
             * */
            CustomerOrder finalCustomerOrder = customerOrder;
            List<CustomerOrderItem> orderItems = trxRequest.getOrderItems().stream().map(item -> {
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

            return new GatewayTransactionResponseDTO(token, redirectUrl);
        }  catch (MidtransError e) {
            throw new RuntimeException("Midtrans error: " + e.getMessage(), e);
        }
    }
}
