package com.example.WaterDelivery.services;

import com.example.WaterDelivery.providers.*;
import com.example.WaterDelivery.providers.Key;
import com.example.WaterDelivery.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class EncryptionService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KeyRepository keyRepository;

    @Autowired
    private UserContactRepository userContactRepository;

    @Autowired
    private MessageRepository messageRepository;

    // 1. Генерация или обновление ключей
    public void generateKeys(Long userId, String method) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        switch (method.toLowerCase()) {
            case "caesar":
                generateOrUpdateCaesarKey(user);
                break;
            case "aes":
                generateOrUpdateAESKey(user);
                break;
            case "rsa":
                generateOrUpdateRSAKeys(user);
                break;
            default:
                throw new IllegalArgumentException("Unsupported encryption method");
        }
    }

    // Метод для генерации или обновления ключа Цезаря
    private void generateOrUpdateCaesarKey(User user) {
        int shift = new SecureRandom().nextInt(33); // Сдвиг от 0 до 32
        Optional<Key> existingKeyOpt = keyRepository.findByUserIdAndMethod(user.getId(), "caesar");
        Key caesarKey;
        if (existingKeyOpt.isPresent()) {
            caesarKey = existingKeyOpt.get();
            caesarKey.setKeyData(String.valueOf(shift));
        } else {
            caesarKey = new Key();
            caesarKey.setUser(user);
            caesarKey.setMethod("caesar");
            caesarKey.setKeyData(String.valueOf(shift));
        }
        keyRepository.save(caesarKey);
        System.out.println("Сгенерирован сдвиг для Цезаря: " + shift);
    }

    // Метод для генерации или обновления ключа AES
    private void generateOrUpdateAESKey(User user) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        SecretKey aesKey = keyGen.generateKey();
        String encodedKey = Base64.getEncoder().encodeToString(aesKey.getEncoded());

        Optional<Key> existingKeyOpt = keyRepository.findByUserIdAndMethod(user.getId(), "aes");
        Key aesKeyEntity;
        if (existingKeyOpt.isPresent()) {
            aesKeyEntity = existingKeyOpt.get();
            aesKeyEntity.setKeyData(encodedKey);
        } else {
            aesKeyEntity = new Key();
            aesKeyEntity.setUser(user);
            aesKeyEntity.setMethod("aes");
            aesKeyEntity.setKeyData(encodedKey);
        }
        keyRepository.save(aesKeyEntity);
        System.out.println("Сгенерирован AES ключ: " + encodedKey);
    }

    // Метод для генерации или обновления ключей RSA
    private void generateOrUpdateRSAKeys(User user) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        String publicKeyStr = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        String privateKeyStr = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());

        // Обновление публичного ключа
        Optional<Key> existingPublicKeyOpt = keyRepository.findByUserIdAndMethod(user.getId(), "rsa_public");
        Key publicKey;
        if (existingPublicKeyOpt.isPresent()) {
            publicKey = existingPublicKeyOpt.get();
            publicKey.setKeyData(publicKeyStr);
        } else {
            publicKey = new Key();
            publicKey.setUser(user);
            publicKey.setMethod("rsa_public");
            publicKey.setKeyData(publicKeyStr);
        }
        keyRepository.save(publicKey);

        // Обновление приватного ключа
        Optional<Key> existingPrivateKeyOpt = keyRepository.findByUserIdAndMethod(user.getId(), "rsa_private");
        Key privateKey;
        if (existingPrivateKeyOpt.isPresent()) {
            privateKey = existingPrivateKeyOpt.get();
            privateKey.setKeyData(privateKeyStr);
        } else {
            privateKey = new Key();
            privateKey.setUser(user);
            privateKey.setMethod("rsa_private");
            privateKey.setKeyData(privateKeyStr);
        }
        keyRepository.save(privateKey);

        System.out.println("Сгенерированы RSA ключи для пользователя ID: " + user.getId());
    }

    // 2. Добавление или обновление контакта с публичным ключом
    public void addOrUpdateContact(Long userId, Long contactId, String method, String publicKeyStr) throws Exception {
        if (userId.equals(contactId)) {
            throw new IllegalArgumentException("Пользователь не может добавить себя в контакты");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        User contact = userRepository.findById(contactId)
                .orElseThrow(() -> new IllegalArgumentException("Контактный пользователь не найден"));

        // Проверка существования контакта
        Optional<UserContact> existingContactOpt = userContactRepository.findByUserAndContactAndMethod(user, contact, method.toLowerCase());
        UserContact userContact;
        if (existingContactOpt.isPresent()) {
            userContact = existingContactOpt.get();
            userContact.setPublicKey(publicKeyStr);
        } else {
            userContact = new UserContact();
            userContact.setUser(user);
            userContact.setContact(contact);
            userContact.setMethod(method.toLowerCase());
            userContact.setPublicKey(publicKeyStr);
        }
        userContactRepository.save(userContact);
        System.out.println("Публичный ключ контакта сохранён для пользователя ID: " + userId);
    }

    // 3. Получение публичного ключа контакта
    public String getContactPublicKey(Long userId, Long contactId, String method) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        User contact = userRepository.findById(contactId)
                .orElseThrow(() -> new IllegalArgumentException("Контактный пользователь не найден"));

        UserContact userContact = userContactRepository.findByUserAndContactAndMethod(user, contact, method.toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Публичный ключ контакта не найден"));

        return userContact.getPublicKey();
    }

    // 4. Шифрование сообщения и сохранение
    public void sendMessage(Long senderId, Long receiverId, String message, String method) throws Exception {
        // Получение публичного ключа получателя
        String publicKeyStr = getContactPublicKey(senderId, receiverId, method);
        PublicKey receiverPublicKey = getPublicKeyFromString(publicKeyStr, method);

        // Шифрование сообщения
        String encryptedMessage = encryptMessage(message, receiverPublicKey, method);

        // Сохранение сообщения
        saveEncryptedMessage(senderId, receiverId, encryptedMessage, method);
    }

    // Конвертация публичного ключа из строки
    private PublicKey getPublicKeyFromString(String publicKeyStr, String method) throws Exception {
        if (method.equalsIgnoreCase("rsa")) {
            byte[] decoded = Base64.getDecoder().decode(publicKeyStr);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(spec);
        }
        // Добавьте поддержку других методов шифрования по мере необходимости
        throw new IllegalArgumentException("Unsupported encryption method for public key conversion");
    }

    // Шифрование сообщения
    private String encryptMessage(String message, PublicKey publicKey, String method) throws Exception {
        switch (method.toLowerCase()) {
            case "rsa":
                return rsaEncrypt(message, publicKey);
            // Добавьте другие методы шифрования по мере необходимости
            default:
                throw new IllegalArgumentException("Unsupported encryption method for encryption");
        }
    }

    // Сохранение зашифрованного сообщения
    private void saveEncryptedMessage(Long senderId, Long receiverId, String encryptedMessage, String method) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Отправитель не найден"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Получатель не найден"));

        Message messageEntity = new Message();
        messageEntity.setSender(sender);
        messageEntity.setReceiver(receiver);
        messageEntity.setEncryptedMessage(encryptedMessage);
        messageEntity.setMethod(method.toLowerCase());
        messageEntity.setTimestamp(LocalDateTime.now());

        messageRepository.save(messageEntity);
        System.out.println("Сообщение сохранено от пользователя ID: " + senderId + " к пользователю ID: " + receiverId);
    }

    // RSA Шифрование
    private String rsaEncrypt(String message, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encrypted = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    // RSA Дешифрование
    public String rsaDecryptMessage(Long receiverId, String encryptedMessage) throws Exception {
        PrivateKey receiverPrivateKey = getRSAPrivateKey(receiverId);
        return rsaDecrypt(encryptedMessage, receiverPrivateKey);
    }

    private PrivateKey getRSAPrivateKey(Long userId) throws Exception {
        Key key = keyRepository.findByUserIdAndMethod(userId, "rsa_private")
                .orElseThrow(() -> new IllegalArgumentException("RSA private key not found for user"));
        byte[] decoded = Base64.getDecoder().decode(key.getKeyData());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }

    private String rsaDecrypt(String encryptedMessage, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decoded = Base64.getDecoder().decode(encryptedMessage);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    // Получение всех расшифрованных сообщений пользователя
    public List<String> getAllDecryptedMessages(Long receiverId) {
        List<Message> messages = messageRepository.findByReceiverId(receiverId);
        List<String> decryptedMessages = new ArrayList<>();
        for (Message msg : messages) {
            try {
                String decrypted = rsaDecryptMessage(receiverId, msg.getEncryptedMessage());
                decryptedMessages.add("От: " + msg.getSender().getUsername() + " - " + decrypted);
            } catch (Exception e) {
                decryptedMessages.add("Ошибка дешифрования сообщения ID " + msg.getId());
            }
        }
        return decryptedMessages;
    }
}
