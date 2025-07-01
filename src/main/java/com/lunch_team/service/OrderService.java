package com.lunch_team.service;

import com.lunch_team.dto.FoodItemRequest;
import com.lunch_team.dto.OrderRequest;
import com.lunch_team.entity.*;
import com.lunch_team.repository.FoodItemRepository;
import com.lunch_team.repository.OrderItemRepository;
import com.lunch_team.repository.OrderRepository;
import com.lunch_team.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final FoodItemRepository foodItemRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public Order createOrder(OrderRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order() {
        };
        order.setUser(user);
        order.setOrderDate(new Date());
        order.setPaymentMethod(request.getPaymentMethod());

        List<OrderItem> orderItems = request.getFoodItems().stream()
                .map(this::createOrderItem)
                .collect(Collectors.toList());

        order.setOrderItems(orderItems);
        calculateTotalAmount(order);

        return orderRepository.save(order);
    }

    private OrderItem createOrderItem(FoodItemRequest foodItemRequest) {
        FoodItem foodItem = foodItemRepository.findById(foodItemRequest.getFoodItemId())
                .orElseThrow(() -> new RuntimeException("Food item not found"));

        OrderItem orderItem = new OrderItem();
        orderItem.setFoodItem(foodItem);
        orderItem.setQuantity(foodItemRequest.getQuantity());
        orderItem.setAmount(foodItem.getPrice().multiply(BigDecimal.valueOf(foodItemRequest.getQuantity())));

        return orderItemRepository.save(orderItem);
    }

    private void calculateTotalAmount(Order order) {
        BigDecimal total = order.getOrderItems().stream()
                .map(OrderItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(total);

        if (order.getPaymentMethod() == PaymentMethod.EVEN_SPLIT) {
            updateEvenSplitPayment(order);
        } else {
            updateItemBasedPayment(order);
        }
    }

    private void updateEvenSplitPayment(Order order) {
        BigDecimal amountPerPerson = order.getTotalAmount().divide(
                BigDecimal.valueOf(order.getOrderItems().size()),
                2,
                RoundingMode.HALF_UP
        );

        order.getUser().setDeposit(
                order.getUser().getDeposit().subtract(amountPerPerson)
        );
    }

    private void updateItemBasedPayment(Order order) {
        // Logic for item-based payment
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Transactional
    public Order updatePaymentMethod(Long id, PaymentMethod paymentMethod) {
        Order order = getOrderById(id);
        order.setPaymentMethod(paymentMethod);

        if (paymentMethod == PaymentMethod.EVEN_SPLIT) {
            updateEvenSplitPayment(order);
        } else {
            updateItemBasedPayment(order);
        }

        return orderRepository.save(order);
    }

}