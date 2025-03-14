package com.warehub.warehub.usecase.user.impl;

import com.warehub.warehub.usecase.user.GeocodingUsecase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class GeocodingUsecaseImpl implements GeocodingUsecase {
    @Value("${rajaongkir.api.key}")
    private String rajaOngkirApiKey;

    @Override
    public String getPostalCodeFromCoordinates(double latitude, double longitude) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://nominatim.openstreetmap.org/reverse?format=json&lat=" + latitude + "&lon=" + longitude;

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        Map<String, Object> address = (Map<String, Object>) response.getBody().get("address");

        if (address == null) {
            return null;
        }

        // Coba ambil kode pos dari OSM terlebih dahulu
        if (address.containsKey("postcode")) {
            return address.get("postcode").toString();
        }

        // Jika tidak ada, coba cari dengan RajaOngkir berdasarkan village, county, atau state
        String[] locationKeys = {"village", "county", "state"};
        for (String key : locationKeys) {
            if (address.containsKey(key)) {
                String searchQuery = address.get(key).toString();
                String postalCode = getPostalCodeFromRajaOngkir(searchQuery);
                if (postalCode != null) {
                    return postalCode;
                }
            }
        }

        return null;
    }

    private String getPostalCodeFromRajaOngkir(String locationName) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://rajaongkir.komerce.id/api/v1/destination/domestic-destination?search=" + locationName;

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set("key", rajaOngkirApiKey);
        org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, entity, Map.class);
        Map<String, Object> responseData = response.getBody();

        if (responseData != null && responseData.containsKey("data")) {
            List<Map<String, Object>> dataList = (List<Map<String, Object>>) responseData.get("data");
            for (Map<String, Object> item : dataList) {
                if (item.containsKey("zip_code")) {
                    return item.get("zip_code").toString();
                }
            }
        }
        return null;
    }
}