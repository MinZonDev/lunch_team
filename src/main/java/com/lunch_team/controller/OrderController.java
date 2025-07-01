package com.lunch_team.controller;

import com.lunch_team.dto.OrderRequest;
import com.lunch_team.entity.Order;
import com.lunch_team.entity.PaymentMethod;
import com.lunch_team.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest request) {
        Order order = (Order) orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}/payment-method")
    public ResponseEntity<Order> updatePaymentMethod(
            @PathVariable Long id,
            @RequestBody PaymentMethod paymentMethod
    ) {
        Order order = orderService.updatePaymentMethod(id, paymentMethod);
        return ResponseEntity.ok(order);
    }
}
