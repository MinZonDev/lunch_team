package com.lunch_team.controller;

import com.lunch_team.dto.MenuResponse;
import com.lunch_team.service.MenuService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/menu")
public class MenuController {
    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    public ResponseEntity<MenuResponse> getMenu() {
        return ResponseEntity.ok(menuService.fetchMenuFromBEFood());
    }
}