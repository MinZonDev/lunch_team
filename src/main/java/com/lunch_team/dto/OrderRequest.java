package com.lunch_team.dto;

import com.lunch_team.entity.PaymentMethod;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    private Long userId;
    private List<FoodItemRequest> foodItems;
    private PaymentMethod paymentMethod;
}
