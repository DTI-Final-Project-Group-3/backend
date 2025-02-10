package com.warehub.warehub.common.utils;

import com.warehub.warehub.common.enums.LocationConstants;

public class LocationService {

    private static boolean getValidLongitude(Double longitude) {
        return longitude != null && !(longitude < LocationConstants.MIN_LONGITUDE.getValue()) && !(longitude > LocationConstants.MAX_LONGITUDE.getValue());
    }

    private static boolean getValidLatitude(Double latitude) {
        return latitude != null && !(latitude < LocationConstants.MIN_LATITUDE.getValue()) && !(latitude > LocationConstants.MAX_LATITUDE.getValue());
    }

    public static Location validateLocation(Double longitude, Double latitude){

        if (!getValidLongitude(longitude) || !getValidLatitude(latitude)){
            return new Location(LocationConstants.DEFAULT_LONGITUDE.getValue(),LocationConstants.DEFAULT_LATITUDE.getValue());
        }
        return new Location(longitude, latitude);
    }
}
