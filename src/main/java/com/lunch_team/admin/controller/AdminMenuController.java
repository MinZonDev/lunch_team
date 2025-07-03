package com.lunch_team.admin.controller;

import com.lunch_team.dto.CreateMenuDto;
import com.lunch_team.dto.FetchedMenuItemDto;
import com.lunch_team.dto.MenuDto;
import com.lunch_team.dto.RestaurantRequestDto;
import com.lunch_team.dto.response.ApiResponse;
import com.lunch_team.exception.BusinessException;
import com.lunch_team.service.MenuService;
import com.lunch_team.util.ResponseUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/restaurants")
@PreAuthorize("hasRole('ADMIN')")
public class AdminMenuController {

    private final MenuService menuService;

    public AdminMenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @PostMapping("/fetch-menu")
    public ResponseEntity<ApiResponse<List<FetchedMenuItemDto>>> fetchRestaurantMenu(
            @RequestBody RestaurantRequestDto requestDto) {
        try {
            List<FetchedMenuItemDto> menuItems = menuService.fetchMenuFromExternalApi(requestDto.getRestaurantId());
            return ResponseUtil.success("Lấy menu nhà hàng thành công", menuItems);
        } catch (Exception e) {
            // Nên có một exception handler để xử lý các lỗi cụ thể hơn
            throw new BusinessException("Không thể lấy menu từ nhà hàng: " + e.getMessage());
        }
    }

    @PostMapping("/menus")
    public ResponseEntity<ApiResponse<MenuDto>> createMenu(
            @Valid @RequestBody CreateMenuDto createMenuDto,
            Authentication authentication) {

        String username = authentication.getName();
        MenuDto newMenu = menuService.createMenu(createMenuDto, username);
        return ResponseUtil.success("Tạo menu thành công", newMenu);
    }
}