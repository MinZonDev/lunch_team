package com.lunch_team.service;

import com.lunch_team.dto.MenuResponse;
import com.lunch_team.entity.RestaurantMenu;
import com.lunch_team.repository.FoodItemRepository;
import com.lunch_team.repository.RestaurantMenuRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestTemplate;


@Service
public class MenuService {
    private final RestaurantMenuRepository menuRepository;
    private final FoodItemRepository foodItemRepository;
    private final RestTemplate restTemplate;

    public MenuService(RestaurantMenuRepository menuRepository,
                       FoodItemRepository foodItemRepository,
                       RestTemplate restTemplate) {
        this.menuRepository = menuRepository;
        this.foodItemRepository = foodItemRepository;
        this.restTemplate = restTemplate;
    }

    public MenuResponse fetchMenuFromBEFood() {
        String url = "${be.food.api.url}";
        String token = "${be.food.api.token}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + token);

        String requestBody = """
            {
                "restaurant_id": "23992",
                "locale": "vi",
                "app_version": "11280",
                "version": "1.1.280",
                "device_type": 3,
                "operator_token": "0b28e008bc323838f5ec84f718ef11e6",
                "customer_package_name": "xyz.be.food",
                "device_token": "8cf04e281318af421dc03fc482e00bfd",
                "ad_id": "",
                "screen_width": 360,
                "screen_height": 640,
                "client_info": {
                    "locale": "vi",
                    "app_version": "11280",
                    "version": "1.1.280",
                    "device_type": 3,
                    "operator_token": "0b28e008bc323838f5ec84f718ef11e6",
                    "customer_package_name": "xyz.be.food",
                    "device_token": "8cf04e281318af421dc03fc482e00bfd",
                    "ad_id": "",
                    "screen_width": 360,
                    "screen_height": 640
                },
                "latitude": 10.77253621500006,
                "longitude": 106.69798153800008
            }
            """;

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<MenuResponse> response = restTemplate.postForEntity(url, request, MenuResponse.class);

        return response.getBody();
    }
}