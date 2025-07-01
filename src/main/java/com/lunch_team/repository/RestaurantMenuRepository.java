package com.lunch_team.repository;

import com.lunch_team.entity.RestaurantMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantMenuRepository extends JpaRepository<RestaurantMenu, Long> {
    RestaurantMenu findByRestaurantId(String restaurantId);
}
