package com.example.WaterDelivery.controller;

import com.example.WaterDelivery.providers.Message;
import com.example.WaterDelivery.providers.Person;
import com.example.WaterDelivery.services.EncryptionService;
import com.example.WaterDelivery.services.MessageService;
import com.example.WaterDelivery.services.PersonService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class MessageController {
    private final EncryptionService encryptionService;
    private final MessageService messageService;
    private final PersonService personService;
    private static final Logger logger = LoggerFactory.getLogger(EncryptionService.class);

    @Autowired
    public MessageController(EncryptionService encryptionService, MessageService messageService, PersonService personService) {
        this.encryptionService = encryptionService;
        this.messageService = messageService;
        this.personService = personService;
    }



    @GetMapping("/sendMessage/{receiverUsername}")
    public String sendMessagePage(@PathVariable String receiverUsername, Model model) {
        model.addAttribute("receiverUsername", receiverUsername);
        return "sendMessage";
    }




    private String encryptMessage(String content) {
        // Простой пример шифрования (это всего лишь для иллюстрации)
        return new StringBuilder(content).reverse().toString();  // Просто переворачиваем текст
    }
    @PostMapping("/decryptMessage")
    public String decryptMessage(
            @RequestParam int messageId,
            @RequestParam String privateKey,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        if (authentication != null) {
            Person receiver = personService.getPerson(authentication.getName()).orElse(null);
            if (receiver == null) {
                redirectAttributes.addFlashAttribute("error", "User not found.");
                return "redirect:/messages";
            }

            Optional<Message> messageOpt = messageService.getMessageById(messageId);
            if (messageOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Message not found.");
                return "redirect:/messages";
            }

            Message message = messageOpt.get();
            if (!(message.getReceiver().getId() == receiver.getId())) {
                redirectAttributes.addFlashAttribute("error", "You are not authorized to decrypt this message.");
                return "redirect:/messages";

            }
            try {
                String decryptedContent;
                if ("RSA".equalsIgnoreCase(message.getEncryptionMethod())) {
                    decryptedContent = encryptionService.decryptWithRSA(message.getContent(), privateKey);
                } else if ("AES".equalsIgnoreCase(message.getEncryptionMethod())) {
                    // Split the encrypted AES key and the encrypted message
                    String[] parts = message.getContent().split(":");
                    if (parts.length != 2) {
                        redirectAttributes.addFlashAttribute("error", "Invalid message format.");
                        return "redirect:/messages";
                    }
                    String encryptedAESKey = parts[0];
                    String encryptedMessage = parts[1];

                    // Decrypt the AES key with receiver's private key
                    String aesKey = encryptionService.decryptWithRSA(encryptedAESKey, privateKey);
                    // Decrypt the message with the AES key
                    decryptedContent = encryptionService.decryptWithAES(encryptedMessage, aesKey);
                } else {
                    redirectAttributes.addFlashAttribute("error", "Unsupported encryption method.");
                    return "redirect:/messages";
                }

                // Store decrypted content temporarily, e.g., in session or pass as a parameter
                // For simplicity, we'll pass it as a flash attribute with the message ID
                redirectAttributes.addFlashAttribute("decryptedMessages", Map.of(messageId, decryptedContent));
                redirectAttributes.addFlashAttribute("success", "Message decrypted successfully.");
            } catch (Exception e) {
                // Handle decryption error
                logger.error("Error decrypting message: {}", e.getMessage());
                redirectAttributes.addFlashAttribute("error", "Decryption error occurred.");
            }
        }
        return "redirect:/messages";
    }


}
