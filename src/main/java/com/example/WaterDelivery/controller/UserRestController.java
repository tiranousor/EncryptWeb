package com.example.WaterDelivery.controller;

import com.example.WaterDelivery.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserRestController {
    private final CartService cartService;

    @Autowired
    public UserRestController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add-order/{person_id}/{waterBottle_id}")
    public ResponseEntity<?> addOrder(@PathVariable int person_id, @PathVariable int waterBottle_id){
        try {
            cartService.saveCart(person_id, waterBottle_id);
            return ResponseEntity.ok().body("{\"status\":\"success\", \"message\":\"Item added to cart\"}");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"status\":\"error\", \"message\":\"" + e.getMessage() + "\"}");
        }
    }
}

