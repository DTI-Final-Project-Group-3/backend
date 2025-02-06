package com.warehub.warehub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "warehouses")
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "warehouses_id_gen")
    @SequenceGenerator(name = "warehouses_id_gen", sequenceName = "warehouses_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @Size(max = 500)
    @Column(name = "detail_address", length = 500)
    private String detailAddress;

    @Size(max = 255)
    @Column(name = "latitude")
    private String latitude;

    @Size(max = 255)
    @Column(name = "longitude")
    private String longitude;

    @Column(name = "descriptions", length = Integer.MAX_VALUE)
    private String descriptions;

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

    @OneToMany(mappedBy = "warehouse")
    private Set<CustomerOrderItem> customerOrderItems;

    @OneToMany(mappedBy = "warehouse")
    private Set<CustomerOrder> customerOrders = new LinkedHashSet<>();

    @OneToMany(mappedBy = "originWarehouse")
    private Set<ProductMutation> productMutations = new LinkedHashSet<>();

    @OneToMany(mappedBy = "warehouse")
    private Set<WarehouseAdmin> warehouseAdmins = new LinkedHashSet<>();

    @OneToMany(mappedBy = "warehouse")
    private Set<WarehouseInventory> warehouseInventories = new LinkedHashSet<>();

}