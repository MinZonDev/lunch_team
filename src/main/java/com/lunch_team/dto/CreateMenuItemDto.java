package com.lunch_team.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data

public class CreateMenuItemDto {
    private String name;
    private BigDecimal price;
    private Long originalItemId; // Null nếu là món custom
    private boolean isCustom;
}
