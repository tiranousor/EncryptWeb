package com.example.WaterDelivery.services;

import com.example.WaterDelivery.providers.*;
import com.example.WaterDelivery.providers.Key;
import com.example.WaterDelivery.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.*;
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

    private void generateOrUpdateCaesarKey(User user) {
        int shift = new SecureRandom().nextInt(32) + 1;
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

    private void generateOrUpdateRSAKeys(User user) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        String publicKeyStr = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        String privateKeyStr = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());

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

    public void sendPublicKey(Long fromUserId, Long toUserId, String method) throws Exception {
        User fromUser = userRepository.findById(fromUserId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь-отправитель не найден"));

        User toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь-получатель не найден"));

        String keyMethod = method.toLowerCase();

        String keyData;
        if (keyMethod.equals("rsa")) {
            Key publicKey = keyRepository.findByUserIdAndMethod(fromUserId, "rsa_public")
                    .orElseThrow(() -> new IllegalArgumentException("Публичный ключ RSA не найден"));
            keyData = publicKey.getKeyData();
        } else if (keyMethod.equals("aes") || keyMethod.equals("caesar")) {
            Key symmetricKey = keyRepository.findByUserIdAndMethod(fromUserId, keyMethod)
                    .orElseThrow(() -> new IllegalArgumentException("Ключ " + method + " не найден"));
            keyData = symmetricKey.getKeyData();
        } else {
            throw new IllegalArgumentException("Unsupported encryption method");
        }

        Optional<UserContact> existingContactOpt = userContactRepository.findByUserAndContactAndMethod(toUser, fromUser, keyMethod);
        UserContact userContact;
        if (existingContactOpt.isPresent()) {
            userContact = existingContactOpt.get();
            userContact.setPublicKey(keyData);
        } else {
            userContact = new UserContact();
            userContact.setUser(toUser);
            userContact.setContact(fromUser);
            userContact.setMethod(keyMethod);
            userContact.setPublicKey(keyData);
        }
        userContactRepository.save(userContact);
        System.out.println("Пользователь ID: " + fromUserId + " отправил ключ (" + method + ") пользователю ID: " + toUserId);
    }

    public void fetchAndStoreContactPublicKey(Long userId, Long contactId, String method) throws Exception {
        System.out.println("Ключ уже сохранён в методе sendPublicKey");
    }

    public String encryptMessageWithContactKey(Long userId, Long contactId, String method, String message) throws Exception {
        String keyMethod = method.toLowerCase();
        UserContact userContact = userContactRepository.findByUserIdAndContactIdAndMethod(userId, contactId, keyMethod)
                .orElseThrow(() -> new IllegalArgumentException("Ключ контакта не найден"));

        String keyData = userContact.getPublicKey();

        return encryptMessage(message, keyData, method);
    }

    public String encryptAndSendMessage(Long senderId, Long receiverId, String method, String message) throws Exception {
        String encryptedMessage;

        String keyMethod = method.toLowerCase();

        UserContact userContact = userContactRepository.findByUserIdAndContactIdAndMethod(senderId, receiverId, keyMethod)
                .orElseThrow(() -> new IllegalArgumentException("Ключ получателя не найден"));

        String keyData = userContact.getPublicKey();

        encryptedMessage = encryptMessage(message, keyData, method);

        saveEncryptedMessage(senderId, receiverId, encryptedMessage, method);
        return encryptedMessage;
    }

    public void saveEncryptedMessage(Long senderId, Long receiverId, String encryptedMessage, String method) {
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

    public String decryptMessage(Long userId, String encryptedMessage, String method) throws Exception {
        String keyMethod = method.toLowerCase();

        String keyData;

        if (keyMethod.equals("rsa")) {
            Key key = keyRepository.findByUserIdAndMethod(userId, keyMethod + "_private")
                    .orElseThrow(() -> new IllegalArgumentException("Приватный ключ не найден для пользователя"));
            keyData = key.getKeyData();
            PrivateKey privateKey = getPrivateKeyFromString(keyData, method);
            return decryptMessage(encryptedMessage, privateKey, method);
        } else if (keyMethod.equals("aes") || keyMethod.equals("caesar")) {
            Key key = keyRepository.findByUserIdAndMethod(userId, keyMethod)
                    .orElseThrow(() -> new IllegalArgumentException("Ключ не найден для пользователя"));
            keyData = key.getKeyData();
            return decryptMessage(encryptedMessage, keyData, method);
        } else {
            throw new IllegalArgumentException("Unsupported encryption method for decryption");
        }
    }

    private PrivateKey getPrivateKeyFromString(String keyData, String method) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(keyData);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }

    private String decryptMessage(String encryptedMessage, Object keyObject, String method) throws Exception {
        switch (method.toLowerCase()) {
            case "rsa":
                PrivateKey privateKey = (PrivateKey) keyObject;
                return rsaDecrypt(encryptedMessage, privateKey);
            case "aes":
                String aesKeyStr = (String) keyObject;
                return aesDecrypt(encryptedMessage, aesKeyStr);
            case "caesar":
                String shiftStr = (String) keyObject;
                return caesarDecrypt(encryptedMessage, Integer.parseInt(shiftStr));
            default:
                throw new IllegalArgumentException("Unsupported encryption method for decryption");
        }
    }

    private String encryptMessage(String message, Object keyObject, String method) throws Exception {
        switch (method.toLowerCase()) {
            case "rsa":
                PublicKey publicKey = getPublicKeyFromString((String) keyObject, method);
                return rsaEncrypt(message, publicKey);
            case "aes":
                String aesKeyStr = (String) keyObject;
                return aesEncrypt(message, aesKeyStr);
            case "caesar":
                String shiftStr = (String) keyObject;
                return caesarEncrypt(message, Integer.parseInt(shiftStr));
            default:
                throw new IllegalArgumentException("Unsupported encryption method for encryption");
        }
    }

    private PublicKey getPublicKeyFromString(String publicKeyStr, String method) throws Exception {
        if (method.equalsIgnoreCase("rsa")) {
            byte[] decoded = Base64.getDecoder().decode(publicKeyStr);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(spec);
        }
        throw new IllegalArgumentException("Unsupported encryption method for public key conversion");
    }

    private String rsaEncrypt(String message, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encrypted = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    private String rsaDecrypt(String encryptedMessage, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decoded = Base64.getDecoder().decode(encryptedMessage);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    private String aesEncrypt(String message, String aesKeyStr) throws Exception {
        byte[] decodedKey = Base64.getDecoder().decode(aesKeyStr);
        SecretKey aesKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        byte[] encrypted = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    private String aesDecrypt(String encryptedMessage, String aesKeyStr) throws Exception {
        byte[] decodedKey = Base64.getDecoder().decode(aesKeyStr);
        SecretKey aesKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, aesKey);
        byte[] decodedMessage = Base64.getDecoder().decode(encryptedMessage);
        byte[] decrypted = cipher.doFinal(decodedMessage);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    private String caesarEncrypt(String message, int shift) {
        StringBuilder result = new StringBuilder();
        char[] chars = message.toCharArray();

        for (char c : chars) {
            result.append(shiftChar(c, shift));
        }
        return result.toString();
    }

    private String caesarDecrypt(String encryptedMessage, int shift) {
        return caesarEncrypt(encryptedMessage, -shift);
    }

    private char shiftChar(char c, int shift) {
        char[] alphabetUpper = new char[]{'А','Б','В','Г','Д','Е','Ё','Ж','З','И','Й','К','Л','М','Н','О','П','Р','С','Т','У','Ф','Х','Ц','Ч','Ш','Щ','Ъ','Ы','Ь','Э','Ю','Я'};
        char[] alphabetLower = new char[]{'а','б','в','г','д','е','ё','ж','з','и','й','к','л','м','н','о','п','р','с','т','у','ф','х','ц','ч','ш','щ','ъ','ы','ь','э','ю','я'};
        int alphabetSize = alphabetUpper.length;

        if (Character.isUpperCase(c)) {
            for (int i = 0; i < alphabetSize; i++) {
                if (c == alphabetUpper[i]) {
                    int newIndex = (i + shift + alphabetSize) % alphabetSize;
                    return alphabetUpper[newIndex];
                }
            }
        } else if (Character.isLowerCase(c)) {
            for (int i = 0; i < alphabetSize; i++) {
                if (c == alphabetLower[i]) {
                    int newIndex = (i + shift + alphabetSize) % alphabetSize;
                    return alphabetLower[newIndex];
                }
            }
        }
        return c;
    }
}
