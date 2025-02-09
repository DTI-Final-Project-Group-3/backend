package com.warehub.warehub.common.enums;

import lombok.Data;
import lombok.Getter;

@Getter
public enum LocationConstants {
    DEFAULT_LONGITUDE(106.8456),
    DEFAULT_LATITUDE(-6.2088),

    MIN_LONGITUDE(-180.0),
    MAX_LONGITUDE(180.0),
    MIN_LATITUDE(-90.0),
    MAX_LATITUDE(90.0),

    MAX_DISTANCE_IN_METERS(50000);

    private final double value;

    LocationConstants(double value) {
        this.value = value;
    }

}
