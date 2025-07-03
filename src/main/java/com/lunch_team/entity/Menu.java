package com.lunch_team.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "menus")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID nhà hàng từ API bên ngoài để tham chiếu
    @Column(name = "restaurant_id", nullable = false)
    private String restaurantId;

    @Column(name = "restaurant_name")
    private String restaurantName;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Liên kết với user đã tạo menu này (Admin)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    // Menu này có đang mở để đặt hàng không?
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    // Danh sách các món ăn trong menu
    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MenuItem> menuItems;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}