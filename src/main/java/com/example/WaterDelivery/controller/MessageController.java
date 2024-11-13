package com.example.WaterDelivery.controller;

import com.example.WaterDelivery.providers.Person;
import com.example.WaterDelivery.services.MessageService;
import com.example.WaterDelivery.services.PersonService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class MessageController {

    private final MessageService messageService;
    private final PersonService personService;

    @Autowired
    public MessageController(MessageService messageService, PersonService personService) {
        this.messageService = messageService;
        this.personService = personService;
    }



    @GetMapping("/sendMessage/{receiverUsername}")
    public String sendMessagePage(@PathVariable String receiverUsername, Model model) {
        model.addAttribute("receiverUsername", receiverUsername);
        return "sendMessage";
    }

    @PostMapping("/sendMessage")
    public String sendMessage(@RequestParam String receiverUsername, @RequestParam String content, Authentication authentication) {
        Person sender = personService.getPerson(authentication.getName()).get();
        Person receiver = personService.getPerson(receiverUsername).get();

        // Пример метода шифрования, который можно использовать
        String encryptedContent = encryptMessage(content);

        messageService.sendMessage(sender, receiver, encryptedContent, "AES");
        return "redirect:/messages";
    }

    private String encryptMessage(String content) {
        // Простой пример шифрования (это всего лишь для иллюстрации)
        return new StringBuilder(content).reverse().toString();  // Просто переворачиваем текст
    }
}
