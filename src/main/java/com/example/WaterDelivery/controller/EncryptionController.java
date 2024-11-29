package com.example.WaterDelivery.controller;

import com.example.WaterDelivery.services.EncryptionService;
import com.example.WaterDelivery.repositories.MessageRepository;
import com.example.WaterDelivery.providers.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Контроллер для управления шифрованием и обменом сообщениями.
 */
@RestController
@RequestMapping("/api")
public class    EncryptionController {

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private MessageRepository messageRepository;

    /**
     * 1. /generate - Генерация или обновление ключей.
     * Пример использования:
     * http://localhost:8080/api/generate?userId=1&method=rsa
     */
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

    /**
     * 2. /send_public_key - Добавление или обновление публичного ключа контакта.
     * Пример использования:
     * curl -X POST "http://localhost:8080/api/send_public_key" \
     *      -d "userId=1" \
     *      -d "contactId=2" \
     *      -d "method=rsa" \
     *      -d "publicKey=BASE64_PUBLIC_KEY_STRING"
     */
    @PostMapping("/send_public_key")
    public ResponseEntity<String> sendPublicKey(@RequestParam Long userId,
                                                @RequestParam Long contactId,
                                                @RequestParam String method,
                                                @RequestParam String publicKey) {
        try {
            encryptionService.addOrUpdateContact(userId, contactId, method, publicKey);
            return ResponseEntity.ok("Публичный ключ контакта сохранён");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка сохранения публичного ключа: " + e.getMessage());
        }
    }

    /**
     * 3. /encrypt_and_send - Шифрование и отправка сообщения.
     * Пример использования:
     * curl -X POST "http://localhost:8080/api/encrypt_and_send" \
     *      -d "senderId=1" \
     *      -d "receiverId=2" \
     *      -d "message=Привет, Боб!" \
     *      -d "method=rsa"
     */
    @PostMapping("/encrypt_and_send")
    public ResponseEntity<String> encryptAndSend(@RequestParam Long senderId,
                                                 @RequestParam Long receiverId,
                                                 @RequestParam String message,
                                                 @RequestParam String method) {
        try {
            encryptionService.sendMessage(senderId, receiverId, message, method);
            return ResponseEntity.ok("Сообщение зашифровано и отправлено");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка шифрования/отправки: " + e.getMessage());
        }
    }

    /**
     * 4. /get_encrypted_msg - Получение и дешифрование сообщения.
     * Пример использования:
     * curl -X POST "http://localhost:8080/api/get_encrypted_msg" \
     *      -d "receiverId=2" \
     *      -d "encryptedMessage=ENCRYPTED_MESSAGE_STRING" \
     *      -d "method=rsa"
     */
    @PostMapping("/get_encrypted_msg")
    public ResponseEntity<String> getEncryptedMsg(@RequestParam Long receiverId,
                                                  @RequestParam String encryptedMessage,
                                                  @RequestParam String method) {
        try {
            String decrypted = encryptionService.rsaDecryptMessage(receiverId, encryptedMessage);
            return ResponseEntity.ok(decrypted);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка дешифрования: " + e.getMessage());
        }
    }

    /**
     * 5. /messages - Получение всех расшифрованных сообщений для пользователя.
     * Пример использования:
     * curl -X GET "http://localhost:8080/api/messages?receiverId=2"
     */
    @GetMapping("/messages")
    public ResponseEntity<List<String>> getAllMessages(@RequestParam Long receiverId) {
        try {
            List<String> decryptedMessages = encryptionService.getAllDecryptedMessages(receiverId);
            return ResponseEntity.ok(decryptedMessages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Дополнительный эндпоинт для добавления контакта и их публичного ключа.
     * Является синонимом /send_public_key.
     * Пример использования:
     * curl -X POST "http://localhost:8080/api/add_contact" \
     *      -d "userId=1" \
     *      -d "contactId=2" \
     *      -d "method=rsa" \
     *      -d "publicKey=BASE64_PUBLIC_KEY_STRING"
     */
    @PostMapping("/add_contact")
    public ResponseEntity<String> addContact(@RequestParam Long userId,
                                             @RequestParam Long contactId,
                                             @RequestParam String method,
                                             @RequestParam String publicKey) {
        try {
            encryptionService.addOrUpdateContact(userId, contactId, method, publicKey);
            return ResponseEntity.ok("Контакт добавлен/обновлён успешно");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка добавления контакта: " + e.getMessage());
        }
    }
}
