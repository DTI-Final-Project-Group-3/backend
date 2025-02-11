package com.warehub.warehub.infrastructure.users.dto;

import com.warehub.warehub.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.locationtech.jts.geom.Point;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
public class UserAddressRequestDTO {
    @NotNull
    private String name;

    @NotNull
    private String detailAddress;

    @NotNull
    private double latitude;

    @NotNull
    private double longitude;

    private Boolean isPrimary; // Use wrapper type
}

