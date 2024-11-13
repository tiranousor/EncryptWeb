package com.example.WaterDelivery.controller;

import com.example.WaterDelivery.providers.Message;
import com.example.WaterDelivery.providers.Person;
import com.example.WaterDelivery.services.EncryptionService;
import com.example.WaterDelivery.services.MessageService;
import com.example.WaterDelivery.services.PersonService;
import com.example.WaterDelivery.util.PersonValidator;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
@Controller
public class ClientController {
    private final PersonService personService;
    private final MessageService messageService;
    private final PersonValidator personValidator;
    private static final Logger logger = LoggerFactory.getLogger(EncryptionService.class);
    private final EncryptionService encryptionService;
    @Autowired
    public ClientController(PersonService personService, MessageService messageService, PersonValidator personValidator, EncryptionService encryptionService) {
        this.personService = personService;
        this.messageService = messageService;
        this.personValidator = personValidator;
        this.encryptionService = encryptionService;
    }

    @GetMapping("/")
    public String homePage() {
        // Перенаправляем на страницу сообщений вместо "каталога"
        return "redirect:/messages";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "loginAndRegistration";
    }

    @GetMapping("/registration")
    public String registration(@ModelAttribute Person person) {
        return "registrationPage";
    }

    @PostMapping("/process_registration")
    public String registrationPerson(@Valid @ModelAttribute Person person, BindingResult bindingResult) {
        personValidator.validate(person, bindingResult);

        if (bindingResult.hasErrors())
            return "registrationPage";

        personService.save(person);
        // Перенаправляем на страницу сообщений после успешной регистрации
        return "redirect:/login?registration";

    }

    @GetMapping("/messages")
    public String showMessages(Model model, Authentication authentication) {
        if (authentication != null) {
            Person currentUser = personService.getPerson(authentication.getName()).orElse(null);
            if (currentUser != null) {
                List<Message> receivedMessages = messageService.getMessagesByReceiver(currentUser);
                List<Message> sentMessages = messageService.getMessagesBySender(currentUser);

                model.addAttribute("receivedMessages", receivedMessages);
                model.addAttribute("sentMessages", sentMessages);
                model.addAttribute("person", currentUser);
            }
        }
        return "messages";
    }

    @GetMapping("/send-message")
    public String showSendMessageForm(
            @RequestParam(required = false, defaultValue = "") String search,
            Model model,
            Authentication authentication
    ) {
        if (authentication != null) {
            Person currentUser = personService.getPerson(authentication.getName()).orElse(null);

            if (currentUser != null) {
                List<Person> searchResults = personService.searchUsers(search, currentUser);

                model.addAttribute("person", currentUser);
                model.addAttribute("message", new Message());
                model.addAttribute("searchResults", searchResults);
                model.addAttribute("searchQuery", search);
            }
        }
        return "sendMessage";
    }

    @PostMapping("/send-message")
    public String sendMessage(
            @ModelAttribute Message message,
            @RequestParam int receiverId,
            @RequestParam String encryptionMethod,
            Authentication authentication
    ) {
        if (authentication != null) {
            Person sender = personService.getPerson(authentication.getName()).orElse(null);
            Person receiver = personService.getPersonById(receiverId).orElseThrow(() -> new RuntimeException("Receiver not found"));

            if (sender != null) {
                messageService.sendMessage(sender, receiver, message.getContent(), encryptionMethod);
            }
        }
        return "redirect:/messages";
    }

}
