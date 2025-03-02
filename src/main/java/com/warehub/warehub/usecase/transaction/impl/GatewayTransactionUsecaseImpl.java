package com.warehub.warehub.usecase.transaction.impl;

import com.midtrans.Midtrans;
import com.midtrans.httpclient.SnapApi;
import com.midtrans.httpclient.error.MidtransError;
import com.warehub.warehub.common.enums.LocationConstants;
import com.warehub.warehub.common.exceptions.DataNotFoundException;
import com.warehub.warehub.common.exceptions.ProductMutationStatusNotFoundException;
import com.warehub.warehub.common.exceptions.ProductMutationTypeNotFoundException;
import com.warehub.warehub.common.exceptions.WarehouseInventoryStatusNotFoundException;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class GatewayTransactionUsecaseImpl implements GatewayTransactionUsecase {
    private final CustomerOrderRepository customerOrderRepository;
    private final CustomerOrderItemsRepository customerOrderItemsRepository;
    private final UsersRepository usersRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final CustomerOrderStatusRepository customerOrderStatusRepository;
    private final WarehouseInventoryRepository warehouseInventoryRepository;
    private final ProductMutationTypeRepository productMutationTypeRepository;
    private final ProductMutationStatusRepository productMutationStatusRepository;
    private final ProductMutationRepository productMutationRepository;

    // Set serverKey to Midtrans global config
    static {
        Midtrans.serverKey =  System.getenv("MIDTRANS_SERVER_KEY");
        Midtrans.isProduction = false;
    }

    public GatewayTransactionUsecaseImpl(
            CustomerOrderRepository customerOrderRepository,
            CustomerOrderItemsRepository customerOrderItemsRepository,
            UsersRepository usersRepository,
            WarehouseRepository warehouseRepository,
            ProductRepository productRepository,
            PaymentMethodRepository paymentMethodRepository,
            CustomerOrderStatusRepository customerOrderStatusRepository,
            WarehouseInventoryRepository warehouseInventoryRepository,
            ProductMutationTypeRepository productMutationTypeRepository,
            ProductMutationStatusRepository productMutationStatusRepository,
            ProductMutationRepository productMutationRepository
    ) {
        this.customerOrderRepository = customerOrderRepository;
        this.customerOrderItemsRepository = customerOrderItemsRepository;
        this.usersRepository = usersRepository;
        this.warehouseRepository = warehouseRepository;
        this.productRepository = productRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.customerOrderStatusRepository = customerOrderStatusRepository;
        this.warehouseInventoryRepository = warehouseInventoryRepository;
        this.productMutationTypeRepository = productMutationTypeRepository;
        this.productMutationStatusRepository = productMutationStatusRepository;
        this.productMutationRepository = productMutationRepository;
    }

    @Transactional
    @Override
    public GatewayTransactionResponseDTO createGatewayTransaction(GatewayTransactionRequestDTO trxRequest) {
        try {
            // Validate user
            User user = usersRepository.findById(trxRequest.getUserId())
                    .orElseThrow(() -> new DataNotFoundException("User not found"));
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

            // Select the first (nearest) warehouse
            if (nearbyWarehouses.isEmpty()) throw new DataNotFoundException("No warehouses found nearby");
            WarehouseResponseDTO findNearbyWarehouse = nearbyWarehouses.getFirst();

            /*
             * Validate warehouse, paymentMethod and order status
             * */
            Warehouse warehouse = warehouseRepository.findById(findNearbyWarehouse.getId())
                    .orElseThrow(() -> new DataNotFoundException("Nearest warehouse not found"));
            PaymentMethod paymentMethod = paymentMethodRepository.findById(trxRequest.getPaymentMethodId())
                    .orElseThrow(() -> new DataNotFoundException("Payment method not found"));
            CustomerOrderStatus customerOrderStatus = customerOrderStatusRepository.findById(trxRequest.getOrderStatusId())
                    .orElseThrow(() -> new DataNotFoundException("Order status not found"));

            /*
             * Validate the order items is ready stock in the nearest warehouse
             * */
            for (OrderItemDTO item : trxRequest.getOrderItems()) {
                Product product = productRepository.findById(item.getProductId())
                        .orElseThrow(() -> new DataNotFoundException("Product not found"));

                WarehouseInventory inventory  = warehouseInventoryRepository.findByProductIdAndWarehouseIdAndDeletedAtIsNull(
                        item.getProductId(), findNearbyWarehouse.getId()
                ).orElseThrow(() -> new DataNotFoundException("Product " + product.getName() + " is out of stock"));

                if (inventory.getQuantity() < item.getQuantity()) throw new DataNotFoundException("Not enough stock " + product.getName());

                /*
                 * Update deduct the stock if met the stock needed
                 * */
                int updateQuantity = inventory.getQuantity() - item.getQuantity();
                Long status = (updateQuantity == 0) ? 2L : 1L;

                inventory.setQuantity(updateQuantity);
                warehouseInventoryRepository.save(inventory);

                /*
                * Validate Product auto mutation type and Product status type
                * */
                ProductMutationType productMutationTypeManual = productMutationTypeRepository.findByIdAndDeletedAtIsNull(1L)
                        .orElseThrow(()-> new ProductMutationTypeNotFoundException("Product mutation type with ID not found !"));
                ProductMutationStatus productMutationStatusPending = productMutationStatusRepository.findByIdAndDeletedAtIsNull(2L)
                        .orElseThrow(()-> new ProductMutationStatusNotFoundException("Product mutation status with ID not found !"));

                /*
                 * Add product mutation record
                 * */
                ProductMutation productMutation = new ProductMutation();
                productMutation.setProduct(product);
                productMutation.setQuantity(-item.getQuantity()); // Negative to indicate stock decrease
                productMutation.setRequester(user);
                productMutation.setOriginWarehouse(warehouse);
                productMutation.setProductMutationType(productMutationTypeManual);
                productMutation.setProductMutationStatus(productMutationStatusPending);
//            productMutation.setAcceptedAt(OffsetDateTime.now());
                productMutationRepository.save(productMutation);
            }

            /*
             * Prepare transaction params for Midtrans
             * */
            Map<String, Object> transactionDetails = new HashMap<>();
            transactionDetails.put("order_id", invoiceCode);
            transactionDetails.put("gross_amount", trxRequest.getGrossAmount());
            params.put("transaction_details", transactionDetails);

            // Midtrans transaction
            String token = SnapApi.createTransactionToken(params);
            String redirectUrl = System.getenv("MIDTRANS_API_URL") + token;

            /*
            * Create and save customer order
            * */
            CustomerOrder customerOrder = new CustomerOrder();
            customerOrder.setUser(user);
            customerOrder.setWarehouse(warehouse);
            customerOrder.setPaymentMethod(paymentMethod);
            customerOrder.setGatewayTrxId(token);
            customerOrder.setShippingCost(trxRequest.getShippingCost());
            customerOrder.setTotalAmount(trxRequest.getGrossAmount());
            customerOrder.setOrderStatus(customerOrderStatus);
            customerOrder.setInvoiceCode(invoiceCode);
            customerOrder = customerOrderRepository.save(customerOrder);

            /*
             * Create and save customer order items
             * */
            CustomerOrder finalCustomerOrder = customerOrder;
            List<CustomerOrderItem> orderItems = trxRequest.getOrderItems().stream().map((item) -> {
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
