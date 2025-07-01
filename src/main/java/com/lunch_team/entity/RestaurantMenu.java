package com.lunch_team.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "restaurant_menus")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantMenu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String restaurantId;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL)
    private List<FoodItem> foodItems;
}
