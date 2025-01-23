package com.warehub.warehub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "product_mutation_items")
public class ProductMutationItem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_mutation_items_id_gen")
    @SequenceGenerator(name = "product_mutation_items_id_gen", sequenceName = "product_mutation_items_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private com.warehub.warehub.entity.Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_mutation_id")
    private com.warehub.warehub.entity.ProductMutation productMutation;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

}