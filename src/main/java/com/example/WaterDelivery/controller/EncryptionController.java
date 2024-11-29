package com.example.WaterDelivery.controller;

import com.example.WaterDelivery.dto.DecryptRequest;
import com.example.WaterDelivery.services.EncryptionService;
import com.example.WaterDelivery.repositories.MessageRepository;
import com.example.WaterDelivery.providers.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class EncryptionController {

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private MessageRepository messageRepository;

    @PostMapping("/generate")
    public ResponseEntity<String> generateKeys(@RequestParam Long userId,
                                               @RequestParam String method) {
        try {
            encryptionService.generateKeys(userId, method);
            return ResponseEntity.ok("Ключи сгенерированы для метода: " + method);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка генерации ключей: " + e.getMessage());
        }
    }


    @PostMapping("/send_public_key")
    public ResponseEntity<String> sendPublicKey(@RequestParam Long fromUserId,
                                                @RequestParam Long toUserId,
                                                @RequestParam String method) {
        try {
            encryptionService.sendPublicKey(fromUserId, toUserId, method);
            return ResponseEntity.ok("Публичный ключ отправлен пользователю ID: " + toUserId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка отправки публичного ключа: " + e.getMessage());
        }
    }


    @PostMapping("/get_public_key")
    public ResponseEntity<String> getPublicKey(@RequestParam Long userId,
                                               @RequestParam Long contactId,
                                               @RequestParam String method) {
        try {
            encryptionService.fetchAndStoreContactPublicKey(userId, contactId, method);
            return ResponseEntity.ok("Публичный ключ получен и сохранён");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка получения публичного ключа: " + e.getMessage());
        }
    }


    @PostMapping("/encrypt")
    public ResponseEntity<String> encrypt(@RequestParam Long userId,
                                          @RequestParam Long contactId,
                                          @RequestParam String method,
                                          @RequestParam String message) {
        try {
            String encryptedMessage = encryptionService.encryptMessageWithContactKey(userId, contactId, method, message);
            return ResponseEntity.ok(encryptedMessage);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка шифрования сообщения: " + e.getMessage());
        }
    }


    @PostMapping("/encrypt_and_send")
    public ResponseEntity<String> encryptAndSend(@RequestParam Long senderId,
                                                 @RequestParam Long receiverId,
                                                 @RequestParam String method,
                                                 @RequestParam String message) {
        try {
            String encryptedMessage = encryptionService.encryptAndSendMessage(senderId, receiverId, method, message);
            return ResponseEntity.ok("Сообщение зашифровано и отправлено " + encryptedMessage);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка шифрования/отправки сообщения: " + e.getMessage());
        }
    }


    @PostMapping("/send_encrypted_msg")
    public ResponseEntity<String> sendEncryptedMsg(@RequestParam Long senderId,
                                                   @RequestParam Long receiverId,
                                                   @RequestParam String method,
                                                   @RequestParam String encryptedMessage) {
        try {
            encryptionService.saveEncryptedMessage(senderId, receiverId, encryptedMessage, method);
            return ResponseEntity.ok("Зашифрованное сообщение отправлено");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка отправки сообщения: " + e.getMessage());
        }
    }


    @PostMapping("/get_encrypted_msg")
    public ResponseEntity<String> getEncryptedMsg(@RequestBody DecryptRequest decryptRequest) {
        try {
            String decryptedMessage = encryptionService.decryptMessage(
                    decryptRequest.getReceiverId(),
                    decryptRequest.getEncryptedMessage(),
                    decryptRequest.getMethod()
            );
            return ResponseEntity.ok(decryptedMessage);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка дешифрования сообщения: " + e.getMessage());
        }
    }

}
