package com.lunch_team.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class MenuResponse {
    @JsonProperty("restaurant_id")
    private String restaurantId;

    @JsonProperty("name")
    private String restaurantName;

    @JsonProperty("menu_items")
    private List<MenuItem> menuItems;

    @JsonProperty("status")
    private String status;

    @JsonProperty("message")
    private String message;
}
