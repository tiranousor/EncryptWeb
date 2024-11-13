package com.example.WaterDelivery.controller;

import com.example.WaterDelivery.providers.Person;
import com.example.WaterDelivery.services.EncryptionService;
import com.example.WaterDelivery.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

@RestController
@RequestMapping("/api/encryption")
public class EncryptionController {

    private final EncryptionService encryptionService;
    private final PersonService personService;

    @Autowired
    public EncryptionController(EncryptionService encryptionService, PersonService personService) {
        this.encryptionService = encryptionService;
        this.personService = personService;
    }

    @PostMapping("/generate")
    public String generateKeys(@RequestParam String encryptionMethod, Authentication authentication) {
        Person person = personService.getPerson(authentication.getName()).orElseThrow();
        return encryptionService.generateKeys(person, encryptionMethod);
    }

    @PostMapping("/send_public_key")
    public String sendPublicKey(Authentication authentication) {
        Person person = personService.getPerson(authentication.getName()).orElseThrow();
        return encryptionService.getPublicKey(person);
    }

    @PostMapping("/encrypt")
    public String encrypt(@RequestParam String method, @RequestBody String plainText, Authentication authentication) {
        Person person = personService.getPerson(authentication.getName()).orElseThrow();
        return encryptionService.encrypt(person, plainText, method);
    }

    @PostMapping("/decrypt")
    public String decrypt(@RequestParam String method, @RequestBody String encryptedText, Authentication authentication) {
        Person person = personService.getPerson(authentication.getName()).orElseThrow();
        return encryptionService.decrypt(person, encryptedText, method);
    }
}