package com.warehub.warehub.infrastructure.warehouse.dto;

import com.warehub.warehub.entity.Warehouse;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Coordinate;

import java.time.OffsetDateTime;

@Data
public class WarehouseRequestDTO {
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String detailAddress;

    @NotNull
    private double longitude;

    @NotNull
    private double latitude;

    private String description;

    private OffsetDateTime deletedAt;

    private static final GeometryFactory geometryFactory = new GeometryFactory();

    public Warehouse toEntity() {
        Warehouse warehouse = new Warehouse();
        warehouse.setName(this.name);
        warehouse.setLocation(geometryFactory.createPoint(new Coordinate(this.longitude, this.latitude)));
        warehouse.setDetailAddress(this.detailAddress);
        warehouse.setDescriptions(this.description);
        return warehouse;
    }
}
