package com.lunch_team.dto;

import lombok.Data;

@Data
public class FoodItemRequest {
    private Long foodItemId;
    private int quantity;
}