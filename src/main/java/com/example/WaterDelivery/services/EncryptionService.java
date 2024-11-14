package com.example.WaterDelivery.services;

import com.example.WaterDelivery.providers.Person;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

@Service
public class EncryptionService {

    // Generate RSA Key Pair
    public void generateRSAKeys(Person person) throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        person.setRsaPublicKey(Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));
        person.setRsaPrivateKey(Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()));
    }

    // Generate AES Key
    public String generateAESKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128); // 128-bit AES
        SecretKey secretKey = keyGenerator.generateKey();
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }
    public String generateKeys(Person person, String method) {
        try {
            if (method.equalsIgnoreCase("RSA")) {
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
                keyPairGenerator.initialize(2048);
                KeyPair keyPair = keyPairGenerator.generateKeyPair();

                person.setRsaPublicKey(Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));
                person.setRsaPrivateKey(Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()));
                return "RSA keys generated successfully.";
            } else if (method.equalsIgnoreCase("AES")) {
                KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
                keyGenerator.init(128); // 128-bit AES
                SecretKey secretKey = keyGenerator.generateKey();

                person.setAesKey(Base64.getEncoder().encodeToString(secretKey.getEncoded()));
                return "AES key generated successfully.";
            } else {
                return "Unsupported encryption method.";
            }
        } catch (Exception e) {
            return "Error generating keys: " + e.getMessage();
        }
    }

    /**
     * Retrieves the RSA public key of the provided Person.
     *
     * @param person the Person entity
     * @return the RSA public key as a Base64 encoded string
     */
    public String getPublicKey(Person person) {
        return person.getRsaPublicKey();
    }

    // Encrypt with RSA
    public String encryptWithRSA(String plainText, String publicKeyStr) throws Exception {
        byte[] publicBytes = Base64.getDecoder().decode(publicKeyStr);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicBytes);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = factory.generatePublic(spec);

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encrypted = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    // Decrypt with RSA
    public String decryptWithRSA(String encryptedText, String privateKeyStr) throws Exception {
        byte[] privateBytes = Base64.getDecoder().decode(privateKeyStr);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateBytes);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = factory.generatePrivate(spec);

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        return new String(decrypted);
    }

    // Encrypt with AES
    public String encryptWithAES(String plainText, String aesKeyStr) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(aesKeyStr);
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encrypted = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    // Decrypt with AES
    public String decryptWithAES(String encryptedText, String aesKeyStr) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(aesKeyStr);
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        return new String(decrypted);
    }
    public String decrypt(Person person, String encryptedText, String method) {
        try {
            if (method.equalsIgnoreCase("RSA")) {
                Cipher cipher = Cipher.getInstance("RSA");
                PrivateKey privateKey = KeyFactory.getInstance("RSA")
                        .generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(person.getRsaPrivateKey())));
                cipher.init(Cipher.DECRYPT_MODE, privateKey);
                byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
                return new String(decryptedBytes);
            } else if (method.equalsIgnoreCase("AES")) {
                Cipher cipher = Cipher.getInstance("AES");
                SecretKey secretKey = new SecretKeySpec(Base64.getDecoder().decode(person.getAesKey()), "AES");
                cipher.init(Cipher.DECRYPT_MODE, secretKey);
                byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
                return new String(decryptedBytes);
            } else {
                return "Unsupported decryption method.";
            }
        } catch (Exception e) {
            return "Error decrypting: " + e.getMessage();
        }
    }
    public String encrypt(Person person, String plainText, String method) {
        try {
            if (method.equalsIgnoreCase("RSA")) {
                Cipher cipher = Cipher.getInstance("RSA");
                PublicKey publicKey = KeyFactory.getInstance("RSA")
                        .generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(person.getRsaPublicKey())));
                cipher.init(Cipher.ENCRYPT_MODE, publicKey);
                byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
                return Base64.getEncoder().encodeToString(encryptedBytes);
            } else if (method.equalsIgnoreCase("AES")) {
                Cipher cipher = Cipher.getInstance("AES");
                SecretKey secretKey = new SecretKeySpec(Base64.getDecoder().decode(person.getAesKey()), "AES");
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
                return Base64.getEncoder().encodeToString(encryptedBytes);
            } else {
                return "Unsupported encryption method.";
            }
        } catch (Exception e) {
            return "Error encrypting: " + e.getMessage();
        }
    }
}
