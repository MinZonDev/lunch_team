package com.lunch_team.repository;

import com.lunch_team.entity.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
    List<FoodItem> findByMenuIdAndIsExtraItem(Long menuId, boolean isExtraItem);
}
