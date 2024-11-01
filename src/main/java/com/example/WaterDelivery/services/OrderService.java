package com.example.WaterDelivery.services;

import com.example.WaterDelivery.providers.Cart;
import com.example.WaterDelivery.providers.Order;
import com.example.WaterDelivery.providers.OrderItem;
import com.example.WaterDelivery.providers.Person;
import com.example.WaterDelivery.providers.WaterBottle;
import com.example.WaterDelivery.repositories.CartRepository;
import com.example.WaterDelivery.repositories.OrderItemRepository;
import com.example.WaterDelivery.repositories.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Autowired
    public OrderService(CartRepository cartRepository, OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Transactional
    public void placeOrder(Person person) {
        List<Cart> cartItems = cartRepository.findByPerson(person);
        Order order = new Order();
        order.setPerson(person);
        order.setOrderDate(LocalDateTime.now());

        double totalOrderPrice = 0.0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (Cart cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            // Заменено на WaterBottle
            orderItem.setWaterBottle(cartItem.getWaterBottle());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(cartItem.getQuantity() * cartItem.getWaterBottle().getPrice());

            totalOrderPrice += orderItem.getTotalPrice();
            orderItems.add(orderItem);
        }

        order.setTotalOrderPrice(totalOrderPrice);
        order.setOrderItems(orderItems);

        orderRepository.save(order);
        orderItemRepository.saveAll(orderItems);
        cartRepository.deleteAll(cartItems);
    }

    public List<Order> showAllOrders() {
        return orderRepository.findAll();
    }

    public List<Order> showOrders(Person person) {
        return orderRepository.findAllByPersonOrderByOrderDateDesc(person);
    }

    public void updateOrderStatus(int orderId, String status) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Заказ не найден"));
        order.setStatus(status);
        orderRepository.save(order);
    }
}
