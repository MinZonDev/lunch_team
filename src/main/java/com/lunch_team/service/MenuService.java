package com.lunch_team.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lunch_team.dto.*;
import com.lunch_team.entity.Menu;
import com.lunch_team.entity.MenuItem;
import com.lunch_team.entity.User;
import com.lunch_team.exception.BusinessException;
import com.lunch_team.repository.MenuRepository;
import com.lunch_team.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuService {

    private final MenuRepository menuRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate; // Dùng để gọi API bên ngoài
    private final ObjectMapper objectMapper; // Dùng để parse JSON

    @Autowired
    public MenuService(MenuRepository menuRepository, UserRepository userRepository, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.menuRepository = menuRepository;
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Lấy menu từ API của Be và chuyển thành danh sách món ăn đơn giản.
     *
     * @param restaurantId ID của nhà hàng trên Be.
     * @return Danh sách các món ăn có thể đặt.
     */
    public List<FetchedMenuItemDto> fetchMenuFromExternalApi(String restaurantId) throws Exception {
        final String apiUrl = "https://gw.be.com.vn/api/v1/be-marketplace/web/restaurant/detail";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjowLCJhdWQiOiJndWVzdCIsImV4cCI6MTc1MTQ1MTcyMywiaWF0IjoxNzUxMzY1MzIzLCJpc3MiOiJiZS1kZWxpdmVyeS1nYXRld2F5In0.BnEEHan097Vn2Pe1A6W-jPPnU-yUXbTvV36uYMuisJc");
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Tạo body cho request như trong cURL
//        String requestBodyJson = String.format("{\"restaurant_id\": \"%s\", \"locale\": \"vi\"}", restaurantId);

        String requestBodyJson = """
                {
                    "restaurant_id": "%s",
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
                """.formatted(restaurantId);


        HttpEntity<String> entity = new HttpEntity<>(requestBodyJson, headers);

        String responseJson = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class).getBody();

        // Parse JSON để lấy thông tin
        JsonNode rootNode = objectMapper.readTree(responseJson);
        JsonNode dataNode = rootNode.path("data");

        if (dataNode.isMissingNode()) {
            throw new BusinessException("Không tìm thấy dữ liệu từ API nhà hàng.");
        }

        List<FetchedMenuItemDto> menuItems = new ArrayList<>();
        JsonNode categoriesNode = dataNode.path("categories");

        for (JsonNode category : categoriesNode) {
            JsonNode itemsNode = category.path("items");
            for (JsonNode item : itemsNode) {
                // Chỉ lấy những món đang active (còn bán)
                if (item.path("is_active").asInt() == 1) {
                    long id = item.path("restaurant_item_id").asLong();
                    String name = item.path("item_name").asText();
                    BigDecimal price = new BigDecimal(item.path("price").asDouble());
                    menuItems.add(new FetchedMenuItemDto(id, name, price));
                }
            }
        }
        return menuItems;
    }

    @Transactional
    public MenuDto createMenu(CreateMenuDto createMenuDto, String adminUsername) {
        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new BusinessException("Không tìm thấy người dùng admin."));

        // Vô hiệu hóa tất cả các menu cũ
        menuRepository.findAll().forEach(menu -> menu.setActive(false));

        Menu menu = new Menu();
        menu.setRestaurantId(createMenuDto.getRestaurantId());
        menu.setRestaurantName(createMenuDto.getRestaurantName());
        menu.setCreatedBy(admin);
        menu.setActive(true); // Menu mới tạo sẽ được active

        List<MenuItem> menuItems = createMenuDto.getItems().stream().map(itemDto -> {
            MenuItem menuItem = new MenuItem();
            menuItem.setName(itemDto.getName());
            menuItem.setPrice(itemDto.getPrice());
            menuItem.setOriginalItemId(itemDto.getOriginalItemId());
            menuItem.setCustom(itemDto.isCustom());
            menuItem.setMenu(menu); // Thiết lập quan hệ hai chiều
            return menuItem;
        }).collect(Collectors.toList());

        menu.setMenuItems(menuItems);

        Menu savedMenu = menuRepository.save(menu);
        return convertToDto(savedMenu);
    }

    /**
     * Tìm menu đang active để người dùng đặt hàng.
     *
     * @return DTO của menu đang hoạt động.
     */
    public MenuDto findActiveMenu() {
        Menu activeMenu = menuRepository.findTopByIsActiveOrderByIdDesc(true)
                .orElseThrow(() -> new BusinessException("Hiện tại không có menu nào được mở để đặt hàng."));
        return convertToDto(activeMenu);
    }

    // --- Helper Method ---

    /**
     * Chuyển đổi từ Menu Entity sang Menu DTO.
     *
     * @param menu Entity cần chuyển đổi.
     * @return DTO tương ứng.
     */
    private MenuDto convertToDto(Menu menu) {
        MenuDto menuDto = new MenuDto();
        menuDto.setId(menu.getId());
        menuDto.setRestaurantId(menu.getRestaurantId());
        menuDto.setRestaurantName(menu.getRestaurantName());
//        menuDto.setCreatedAt(menu.getCreatedAt());
        menuDto.setActive(menu.isActive());
        if (menu.getCreatedBy() != null) {
            menuDto.setCreatedByUsername(menu.getCreatedBy().getUsername());
        }

        List<MenuItemDto> menuItemDtos = menu.getMenuItems().stream().map(item -> {
            MenuItemDto itemDto = new MenuItemDto();
            itemDto.setId(item.getId());
            itemDto.setName(item.getName());
            itemDto.setPrice(item.getPrice());
            itemDto.setCustom(item.isCustom());
            return itemDto;
        }).collect(Collectors.toList());

        menuDto.setMenuItems(menuItemDtos);
        return menuDto;
    }
}