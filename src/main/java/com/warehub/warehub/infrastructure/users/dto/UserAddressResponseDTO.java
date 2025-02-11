package com.warehub.warehub.infrastructure.users.dto;

import com.warehub.warehub.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.locationtech.jts.geom.Point;

import java.time.OffsetDateTime;

@Data
public class UserAddressResponseDTO {
    private Long id;
    private String name;
    private String detailAddress;
    private boolean isPrimary;
    private double latitude;
    private double longitude;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
