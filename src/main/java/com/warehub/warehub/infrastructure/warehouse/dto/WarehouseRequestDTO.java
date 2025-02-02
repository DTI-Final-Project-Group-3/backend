package com.warehub.warehub.infrastructure.warehouse.dto;

import com.warehub.warehub.entity.Warehouse;
import lombok.Data;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Coordinate;

@Data
public class WarehouseRequestDTO {
    private String name;
    private String detailAddress;
    private double longitude;
    private double latitude;
    private String description;

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
