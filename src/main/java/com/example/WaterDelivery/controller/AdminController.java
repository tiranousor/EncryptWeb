package com.example.WaterDelivery.controller;

import com.example.WaterDelivery.providers.Order;
import com.example.WaterDelivery.providers.Person;
import com.example.WaterDelivery.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class AdminController {

    private final OrderService orderService;

    @Autowired
    public AdminController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/admin/orders")
    public String getAllOrders(Authentication authentication, Model model) {
        List<Order> orders = orderService.showAllOrders();
        Map<Person, List<Order>> personOrdersMap = orders.stream().collect(Collectors.groupingBy(Order::getPerson));
        model.addAttribute("personOrdersMap", personOrdersMap);
        return "users";
    }
}
