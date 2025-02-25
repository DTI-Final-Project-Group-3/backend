package com.warehub.warehub.usecase.user.impl;

import com.warehub.warehub.usecase.user.GeocodingUsecase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class GeocodingUsecaseImpl implements GeocodingUsecase {
    public String getPostalCodeFromCoordinates(double latitude, double longitude) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://nominatim.openstreetmap.org/reverse?format=json&lat=" + latitude + "&lon=" + longitude;

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        Map<String, Object> address = (Map<String, Object>) response.getBody().get("address");

        if (address == null || !address.containsKey("postcode")) {
            return null;
        }

        return address.get("postcode").toString(); // Get postal code
    }
}

