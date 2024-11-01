package com.example.WaterDelivery.services;

import com.example.WaterDelivery.providers.Cart;
import com.example.WaterDelivery.providers.Person;
import com.example.WaterDelivery.providers.WaterBottle;
import com.example.WaterDelivery.repositories.CartRepository;
import com.example.WaterDelivery.repositories.PersonRepository;
import com.example.WaterDelivery.repositories.WaterBottleRepository;
import com.example.WaterDelivery.repositories.OrderItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final PersonRepository personRepository;
    private final WaterBottleRepository waterBottleRepository;
    private final OrderItemRepository orderItemRepository;

    @Autowired
    public CartService(CartRepository cartRepository, PersonRepository personRepository, WaterBottleRepository waterBottleRepository, OrderItemRepository orderItemRepository) {
        this.cartRepository = cartRepository;
        this.personRepository = personRepository;
        this.waterBottleRepository = waterBottleRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Transactional
    public void deleteAll(int id) {
        cartRepository.deleteAllByWaterBottleId(id);
    }

    public void saveCart(int person_id, int waterBottle_id) {

        Person person = personRepository.findById(person_id).orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        WaterBottle waterBottle = waterBottleRepository.findById(waterBottle_id).orElseThrow(() -> new RuntimeException("Бутылка не найдена"));

        Cart cartStatus = cartRepository.findByPersonIdAndWaterBottleId(person_id, waterBottle_id);

        Cart cart = null;

        if (ObjectUtils.isEmpty(cartStatus)) {
            cart = new Cart();
            cart.setWaterBottle(waterBottle);
            cart.setPerson(person);
            cart.setQuantity(1);
            cart.setTotalPrice(waterBottle.getPrice());
        } else {
            cart = cartStatus;
            cart.setQuantity(cart.getQuantity() + 1);
            cart.setTotalPrice(cart.getQuantity() * cart.getWaterBottle().getPrice());
        }
        cartRepository.save(cart);
    }

    public List<Cart> getListCarts(Person person){
        List<Cart> carts = cartRepository.findByPerson(person);

        double totalcartPrice = 0.0;
        List<Cart> updateCarts = new ArrayList<>();
        for (Cart c : carts) {
            double totalPrice = (c.getWaterBottle().getPrice() * c.getQuantity());
            c.setTotalPrice(totalPrice);
            totalcartPrice += totalPrice;
            c.setTotalOrderPrice(totalcartPrice);

            updateCarts.add(c);
        }

        return updateCarts;
    }

    public Integer getCountCart(int person_id) {
        return cartRepository.countByPersonId(person_id);
    }

    public void updateQuantity(String sy, Integer oId) {

        Cart cart = cartRepository.findById(oId).orElseThrow(() -> new RuntimeException("Элемент корзины не найден"));
        int updateQuantity;

        if (sy.equalsIgnoreCase("-")) {
            updateQuantity = cart.getQuantity() - 1;

            if (updateQuantity <= 0) {
                cartRepository.delete(cart);
            } else {
                cart.setQuantity(updateQuantity);
                cartRepository.save(cart);
            }

        } else {
            updateQuantity = cart.getQuantity() + 1;
            cart.setQuantity(updateQuantity);
            cartRepository.save(cart);
        }

    }

    public List<Cart> getAllcarts(String personRole){
        return cartRepository.findAllByPersonRole(personRole);
    }
}
