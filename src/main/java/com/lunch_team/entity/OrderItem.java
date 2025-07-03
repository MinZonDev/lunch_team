package com.lunch_team.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Liên kết với đơn hàng cha
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // Món ăn được chọn từ Menu
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    @Column(nullable = false)
    private int quantity;

    // Lưu lại giá tại thời điểm đặt hàng để tránh ảnh hưởng nếu giá món ăn thay đổi
    @Column(name = "price_at_order", nullable = false)
    private BigDecimal priceAtOrder;

    @Column(columnDefinition = "TEXT")
    private String note;
}