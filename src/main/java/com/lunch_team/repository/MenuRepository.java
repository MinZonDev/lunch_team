package com.lunch_team.repository;

import com.lunch_team.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    Optional<Menu> findTopByIsActiveOrderByIdDesc(boolean isActive);
}