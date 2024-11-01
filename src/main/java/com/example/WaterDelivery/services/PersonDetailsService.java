package com.example.WaterDelivery.services;

import com.example.WaterDelivery.providers.Person;
import com.example.WaterDelivery.repositories.PersonRepository;
import com.example.WaterDelivery.security.PersonDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PersonDetailsService implements UserDetailsService {
    private final PersonRepository personRepository;

    public PersonDetailsService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Person> person = personRepository.findByUsername(username);
        if (person.isEmpty())
            throw new UsernameNotFoundException("User not found");
        return new PersonDetails(person.get());
    }
}
