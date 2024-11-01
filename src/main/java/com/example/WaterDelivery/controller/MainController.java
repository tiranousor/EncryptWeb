package com.example.WaterDelivery.controller;

import com.example.WaterDelivery.providers.Cart;
import com.example.WaterDelivery.providers.Order;
import com.example.WaterDelivery.providers.Person;
import com.example.WaterDelivery.providers.WaterBottle;
import com.example.WaterDelivery.services.CartService;
import com.example.WaterDelivery.services.OrderService;
import com.example.WaterDelivery.services.PersonService;
import com.example.WaterDelivery.services.WaterBottleService;
import com.example.WaterDelivery.util.FileUploadUtil;
import com.example.WaterDelivery.util.PersonValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

@Controller
public class MainController {
    private final WaterBottleService waterBottleService;
    private final OrderService orderService;
    private final PersonService personService;
    private final CartService cartService;
    private final PersonValidator personValidator;

    @Autowired
    public MainController(WaterBottleService waterBottleService, OrderService orderService, PersonService personService, CartService cartService, PersonValidator personValidator) {
        this.waterBottleService = waterBottleService;
        this.orderService = orderService;
        this.personService = personService;
        this.cartService = cartService;
        this.personValidator = personValidator;
    }

    @GetMapping("/")
    public String homePage(Model model, Authentication authentication) {
        if (authentication != null) {
            model.addAttribute("person_id", personService.getPerson(authentication.getName()).get().getId());
        } model.addAttribute("waterBottle",waterBottleService.findAll());
        return "home";
    }
    @GetMapping("/catalog")
    public String showCatalog(Model model,  Authentication authentication) {
        if (authentication != null) {
            model.addAttribute("person_id", personService.getPerson(authentication.getName()).get().getId());
        } model.addAttribute("waterBottle",waterBottleService.findAll());
        return "catalog";
    }


    @GetMapping("/login")
    public String showLoginPage() {
        return "loginAndRegistration";
    }

    @GetMapping("/registration")
    public String registration(@ModelAttribute Person person){
        return "registrationPage";
    }

    @PostMapping("/process_registration")
    public String registrationPerson(@Valid @ModelAttribute Person person, BindingResult bindingResult) {
        personValidator.validate(person, bindingResult);

        if (bindingResult.hasErrors())
            return "registrationPage";

        personService.save(person);
        return "redirect:/login?registration";
    }

    @GetMapping("/cartQuantityUpdate")
    public String updateCartQuantity(@RequestParam String sy, @RequestParam Integer oId) {
        cartService.updateQuantity(sy, oId);
        return "redirect:/cart";
    }

    private Person getLoggedInPersonDetails(Authentication p) {
        String name = p.getName();
        return personService.getPerson(name).get();
    }

    @GetMapping("/cart")
    public String loadCartPage(Authentication p, Model model){
        Person person = getLoggedInPersonDetails(p);
        List<Cart> orders = cartService.getListCarts(person);
        model.addAttribute("orders", orders);
        if (!orders.isEmpty()) {
            Double totalOrderPrice = orders.get(orders.size() - 1).getTotalOrderPrice();
            model.addAttribute("totalOrderPrice" ,totalOrderPrice);
        }
        return "cart";
    }

    @PostMapping("/cart")
    public String confirmOrder(Authentication authentication) {
        orderService.placeOrder(getLoggedInPersonDetails(authentication));
        return "redirect:/cart";
    }

    @GetMapping("/my-orders")
    public String myOrders(Model model, Authentication authentication) {
        Person person = personService.getPerson(authentication.getName()).get();
        List<Order> orders = orderService.showOrders(person);
        model.addAttribute("orders", orders);
        return "my-orders";
    }
    @GetMapping("/userProfile")
    public String personProfile(Model model, Authentication authentication) {
        String authName = authentication.getName();
        System.out.println("Authenticated Username: " + authName);

        Person person = personService.getPerson(authName)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + authName));

        List<Order> orders = orderService.showOrders(person);

        model.addAttribute("person", person);
        model.addAttribute("orders", orders);

        return "userProfile";
    }
    @GetMapping("/edit/{id}")
    public String editProfile(Model model, @PathVariable("id") int id) {
        Person person = personService.findOne(id);

        model.addAttribute("person", person);
        return "editProfile";
    }

    @PostMapping("/edit/{id}")
    public String updateProfile(Authentication authentication, @PathVariable("id") int id,
                                @Valid Person personForm, BindingResult bindingResult,
                                @RequestParam("avatarFile") MultipartFile avatarFile) {
        if (bindingResult.hasErrors()) {
            return "editProfile";
        }

        Person existingPerson = personService.findOne(id);

        if (!avatarFile.isEmpty()) {
            String originalFileName = avatarFile.getOriginalFilename();
            String extension = "";

            if (originalFileName != null) {
                extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }


            String fileName = existingPerson.getId() + extension;
            String uploadDir = "user-photos/";
            try {
                FileUploadUtil.saveFile(uploadDir, fileName, avatarFile);
                existingPerson.setAvatarUrl("/" + uploadDir + fileName);
            } catch (IOException e) {
                bindingResult.rejectValue("avatarUrl", "error.avatarUrl", e.getMessage());
                return "editProfile";
            }
        }

        // Обновляем данные пользователя
        existingPerson.setUsername(personForm.getUsername());
        existingPerson.setEmail(personForm.getEmail());
        existingPerson.setPhoneNumber(personForm.getPhoneNumber());
        existingPerson.setAddress(personForm.getAddress());
        existingPerson.setAbout(personForm.getAbout());

        personService.update(id, existingPerson);

        // Если имя пользователя изменилось, обновляем объект аутентификации
        if (!authentication.getName().equals(existingPerson.getUsername())) {
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    existingPerson.getUsername(),
                    authentication.getCredentials(),
                    authentication.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }

        return "redirect:/userProfile";
    }


}
