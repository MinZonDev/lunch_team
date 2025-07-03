package com.lunch_team.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FetchedMenuItemDto {
    private Long originalItemId; // restaurant_item_id
    private String name;         // item_name
    private BigDecimal price;    // price
}