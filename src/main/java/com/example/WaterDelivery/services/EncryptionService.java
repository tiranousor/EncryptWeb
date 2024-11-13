package com.example.WaterDelivery.services;

import com.example.WaterDelivery.providers.Person;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
public class EncryptionService {

    public String generateKeys(Person person, String method) {
        try {
            if (method.equalsIgnoreCase("RSA")) {
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
                keyPairGenerator.initialize(2048);
                KeyPair keyPair = keyPairGenerator.generateKeyPair();
                person.setRsaPrivateKey(Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()));
                person.setRsaPublicKey(Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));
                return "RSA keys generated";
            } else if (method.equalsIgnoreCase("AES")) {
                KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
                keyGenerator.init(128);
                SecretKey secretKey = keyGenerator.generateKey();
                person.setAesKey(Base64.getEncoder().encodeToString(secretKey.getEncoded()));
                return "AES key generated";
            } else {
                return "Unsupported encryption method";
            }
        } catch (Exception e) {
            return "Error generating keys: " + e.getMessage();
        }
    }

    public String getPublicKey(Person person) {
        return person.getRsaPublicKey();
    }

    public String encrypt(Person person, String plainText, String method) {
        try {
            if (method.equalsIgnoreCase("RSA")) {
                Cipher cipher = Cipher.getInstance("RSA");
                PublicKey publicKey = KeyFactory.getInstance("RSA")
                        .generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(person.getRsaPublicKey())));
                cipher.init(Cipher.ENCRYPT_MODE, publicKey);
                return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes()));
            } else if (method.equalsIgnoreCase("AES")) {
                Cipher cipher = Cipher.getInstance("AES");
                SecretKey secretKey = new SecretKeySpec(Base64.getDecoder().decode(person.getAesKey()), "AES");
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes()));
            } else {
                return "Unsupported encryption method";
            }
        } catch (Exception e) {
            return "Error encrypting: " + e.getMessage();
        }
    }

    public String decrypt(Person person, String encryptedText, String method) {
        try {
            if (method.equalsIgnoreCase("RSA")) {
                Cipher cipher = Cipher.getInstance("RSA");
                PrivateKey privateKey = KeyFactory.getInstance("RSA")
                        .generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(person.getRsaPrivateKey())));
                cipher.init(Cipher.DECRYPT_MODE, privateKey);
                return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedText)));
            } else if (method.equalsIgnoreCase("AES")) {
                Cipher cipher = Cipher.getInstance("AES");
                SecretKey secretKey = new SecretKeySpec(Base64.getDecoder().decode(person.getAesKey()), "AES");
                cipher.init(Cipher.DECRYPT_MODE, secretKey);
                return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedText)));
            } else {
                return "Unsupported decryption method";
            }
        } catch (Exception e) {
            return "Error decrypting: " + e.getMessage();
        }
    }
}