package com.warehub.warehub.usecase.user.impl;

import com.warehub.warehub.entity.UserAddress;
import com.warehub.warehub.entity.Warehouse;
import com.warehub.warehub.infrastructure.users.dto.ShippingCostRequestDTO;
import com.warehub.warehub.infrastructure.users.dto.ShippingCostResponseDTO;
import com.warehub.warehub.infrastructure.users.repository.UserAddressRepository;
import com.warehub.warehub.infrastructure.warehouse.repository.WarehouseRepository;
import com.warehub.warehub.usecase.user.GeocodingUsecase;
import com.warehub.warehub.usecase.user.ShippingUsecase;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ShippingUsecaseImpl implements ShippingUsecase {
    @Value("${rajaongkir.api.key}")
    private String rajaOngkirApiKey;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private UserAddressRepository userAddressRepository;

    @Autowired
    private GeocodingUsecase geocodingUsecase;

    @Override
    public ShippingCostResponseDTO getCost(ShippingCostRequestDTO requestDTO) {
        Warehouse warehouse = warehouseRepository.findById(requestDTO.getWarehouseId()).get();
        UserAddress userAddress = userAddressRepository.findById(requestDTO.getUserAddressId()).get();
        Point warehouseLocation = warehouse.getLocation();
        Point userAddressLocation = userAddress.getLocation();

        // Convert coordinates to postal codes
        String originPostalCode = geocodingUsecase.getPostalCodeFromCoordinates(warehouseLocation.getY(), warehouseLocation.getX());
        String destinationPostalCode = geocodingUsecase.getPostalCodeFromCoordinates(userAddressLocation.getY(), userAddressLocation.getX());

        if (originPostalCode == null || destinationPostalCode == null) {
            throw new RuntimeException("Failed to retrieve postal codes from coordinates.");
        }

        // Prepare request payload
        Map<String, String> requestBody = Map.of(
                "origin", originPostalCode,
                "destination", destinationPostalCode,
                "weight", String.valueOf((int) requestDTO.getWeight()), // Convert weight to integer
                "courier", requestDTO.getCourier(),
                "price", "lowest"
        );

        ResponseEntity<Map> response = callRajaOngkirAPI(requestBody);
        Map<String, Object> responseData = response.getBody();

        // Parse response
        List<ShippingCostResponseDTO.ShippingCostDetail> costDetails = new ArrayList<>();
        if (responseData != null && responseData.containsKey("data")) {
            List<Map<String, Object>> results = (List<Map<String, Object>>) responseData.get("data");

            for (Map<String, Object> courierResult : results) {
                ShippingCostResponseDTO.ShippingCostDetail detail = new ShippingCostResponseDTO.ShippingCostDetail();
                detail.setService(courierResult.get("service").toString());
                detail.setCost(Float.parseFloat(courierResult.get("cost").toString()));
                detail.setEtd(courierResult.get("etd").toString());
                detail.setName(courierResult.get("name").toString());
                detail.setCode(courierResult.get("code").toString());
                detail.setDescription(courierResult.get("description").toString());
                costDetails.add(detail);
            }
        }

        return new ShippingCostResponseDTO(requestDTO.getCourier(), costDetails);
    }

    @Override
    public ShippingCostResponseDTO getCostDummy(ShippingCostRequestDTO requestDTO) {
        // Buat daftar dummy harga pengiriman
        List<ShippingCostResponseDTO.ShippingCostDetail> costDetails = new ArrayList<>();

        costDetails.add(new ShippingCostResponseDTO.ShippingCostDetail(
                "Pos Reguler", "POS Indonesia (POS)", "pos", "Pos Reguler", 25000.0f, "8 day"));
        costDetails.add(new ShippingCostResponseDTO.ShippingCostDetail(
                "JTR", "Jalur Nugraha Ekakurir (JNE)","jne", "JNE Trucking", 10000.0f, "6 day"));
        costDetails.add(new ShippingCostResponseDTO.ShippingCostDetail(
                "REG", "Jalur Nugraha Ekakurir (JNE)","jne", "Layanan Reguler", 20000.0f, "2 day"));
        costDetails.add(new ShippingCostResponseDTO.ShippingCostDetail(
                "REG", "Citra Van Titipan Kilat (TIKI)","tiki", "Reguler Service", 25000.0f, "2 day"));
        costDetails.add(new ShippingCostResponseDTO.ShippingCostDetail(
                "OKE", "Jalur Nugraha Ekakurir (JNE)","jne", "Ongkos Kirim Ekonomis", 15000.0f, "4 day"));
        return new ShippingCostResponseDTO(requestDTO.getCourier(), costDetails);
    }


    private ResponseEntity<Map> callRajaOngkirAPI(Map<String, String> requestBody) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("key", rajaOngkirApiKey);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        requestBody.forEach(formData::add);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formData, headers);
        return restTemplate.exchange("https://rajaongkir.komerce.id/api/v1/calculate/domestic-cost", HttpMethod.POST, entity, Map.class);
    }
}


