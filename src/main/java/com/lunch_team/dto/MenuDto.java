package com.lunch_team.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class MenuDto {
    private Long id;
    private String restaurantId;
    private String restaurantName;
//    private LocalDateTime createdAt;
    private boolean isActive;
    private String createdByUsername;
    private List<MenuItemDto> menuItems;
}