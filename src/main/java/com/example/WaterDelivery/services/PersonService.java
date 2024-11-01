package com.example.WaterDelivery.services;

import com.example.WaterDelivery.providers.Person;
import com.example.WaterDelivery.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional

public class PersonService {
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PersonService(PersonRepository personRepository, PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }
    public Person findOne(int id) {
        Optional<Person> foundClient = personRepository.findById(id);
        return foundClient.orElse(null);
    }
    public Optional<Person> getPerson(String username) {
        return personRepository.findByUsername(username);
    }
    public Optional<Person> getClientByPhoneNumber(String phoneNumber) {
        return personRepository.findByPhoneNumber(phoneNumber);
    }
    public Optional<Person> getClientByEmail(String email) {
        return personRepository.findByEmail(email);
    }

    @Transactional
    public void save(Person person){
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        personRepository.save(person);
    }
    @Transactional
    public void update(int id, Person updateClient){
        updateClient.setId(id);
        personRepository.save(updateClient);
    }
}
