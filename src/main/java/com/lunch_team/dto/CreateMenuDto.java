package com.lunch_team.dto;

import lombok.Data;
import java.util.List;
import java.math.BigDecimal;

@Data
public class CreateMenuDto {
    private String restaurantId;
    private String restaurantName;
    private List<CreateMenuItemDto> items;
}