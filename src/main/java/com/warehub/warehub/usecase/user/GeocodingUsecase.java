package com.warehub.warehub.usecase.user;

public interface GeocodingUsecase {
    String getPostalCodeFromCoordinates(double latitude, double longitude);
}
