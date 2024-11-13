package com.example.WaterDelivery.services;

import com.example.WaterDelivery.providers.Person;
import com.example.WaterDelivery.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PersonService {
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PersonService(PersonRepository personRepository, PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }
    public List<Person> searchUsers(String search, Person currentUser) {
        return personRepository.findAll().stream()
                .filter(person -> person.getId() != currentUser.getId()) // Исключаем текущего пользователя
                .filter(person -> person.getUsername().toLowerCase().contains(search.toLowerCase())) // Фильтруем по имени
                .toList();
    }


    public Optional<Person> getPerson(String username) {
        return personRepository.findByUsername(username);
    }

    public Optional<Person> getPersonByEmail(String email) {
        return personRepository.findByEmail(email);
    }

    public Optional<Person> getPersonById(int id) {
        return personRepository.findById(id);
    }

    public List<Person> getAllUsersExcept(Person currentUser) {
        return personRepository.findAll().stream()
                .filter(person -> person.getId() != currentUser.getId())
                .toList();
    }

    @Transactional
    public void save(Person person) {
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        personRepository.save(person);
    }
}
