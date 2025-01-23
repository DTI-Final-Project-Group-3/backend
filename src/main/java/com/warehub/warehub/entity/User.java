package com.warehub.warehub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_gen")
    @SequenceGenerator(name = "users_id_gen", sequenceName = "users_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 255)
    @Column(name = "username")
    private String username;

    @Size(max = 255)
    @Column(name = "email")
    private String email;

    @NotNull
    @ColumnDefault("false")
    @Column(name = "is_email_verified", nullable = false)
    private Boolean isEmailVerified = false;

    @Column(name = "password_hash", length = Integer.MAX_VALUE)
    private String passwordHash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    @Size(max = 255)
    @Column(name = "fullname")
    private String fullname;

    @ColumnDefault("'X'")
    @Column(name = "gender", length = Integer.MAX_VALUE)
    private String gender;

    @Size(max = 500)
    @Column(name = "biodata", length = 500)
    private String biodata;

    @Column(name = "birthdate")
    private LocalDate birthdate;

    @Size(max = 40)
    @Column(name = "phone_number", length = 40)
    private String phoneNumber;

    @Column(name = "profile_image_url", length = Integer.MAX_VALUE)
    private String profileImageUrl;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at", nullable = false)
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

    @OneToMany(mappedBy = "customerOrder")
    private Set<CustomerOrderItem> customerOrderItems = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<CustomerOrder> customerOrders = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<EmailVerificationToken> emailVerificationTokens = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<Notification> notifications = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<Oauth2UserInfo> oauth2UserInfos = new LinkedHashSet<>();

    @OneToMany(mappedBy = "requester")
    private Set<ProductMutation> productMutations = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<UserAddress> userAddresses = new LinkedHashSet<>();

    @OneToMany(mappedBy = "userAssigner")
    private Set<com.warehub.warehub.entity.WarehouseAdmin> warehouseAdmins = new LinkedHashSet<>();

}