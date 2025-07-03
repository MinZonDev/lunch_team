package com.lunch_team.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "menu_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Tên món ăn
    @Column(name = "name", nullable = false)
    private String name;

    // Giá tiền, dùng BigDecimal để đảm bảo chính xác
    @Column(name = "price", nullable = false)
    private BigDecimal price;

    // ID của món ăn gốc từ API Be, có thể null nếu là món custom
    @Column(name = "original_item_id")
    private Long originalItemId;

    // Đánh dấu đây có phải là món do admin tự thêm không
    @Column(name = "is_custom", nullable = false)
    private boolean isCustom = false;

    // Liên kết ngược lại với Menu chứa nó
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;
}