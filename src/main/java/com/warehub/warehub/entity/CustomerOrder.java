package com.warehub.warehub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "customer_orders")
public class CustomerOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_orders_id_gen")
    @SequenceGenerator(name = "customer_orders_id_gen", sequenceName = "customer_orders_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private com.warehub.warehub.entity.User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private com.warehub.warehub.entity.Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_method_id")
    private com.warehub.warehub.entity.PaymentMethod paymentMethod;

    @Column(name = "payment_proof_image_url", length = Integer.MAX_VALUE)
    private String paymentProofImageUrl;

    @Column(name = "gateway_trx_id", length = Integer.MAX_VALUE)
    private String gatewayTrxId;

    @Column(name = "shipping_cost", precision = 10, scale = 5)
    private BigDecimal shippingCost;

    @Column(name = "total_amount", precision = 10, scale = 5)
    private BigDecimal totalAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_status_id")
    private CustomerOrderStatus orderStatus;

    @Size(max = 255)
    @Column(name = "invoice_code")
    private String invoiceCode;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @PrePersist
    public void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = OffsetDateTime.now();
        }
        this.updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    @PreRemove
    protected void onRemove() {
        deletedAt = OffsetDateTime.now();
    }

}