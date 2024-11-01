package com.example.WaterDelivery.util;

import com.example.WaterDelivery.providers.Person;
import com.example.WaterDelivery.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PersonValidator implements Validator {
    private final PersonService personService;
    @Autowired
    public PersonValidator(PersonService personService) {
        this.personService = personService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Person person = (Person) target;
        if (personService.getPerson(person.getUsername()).isPresent()) {
            errors.rejectValue("username", "", "Человек с таким именем уже существует");
        }
        if (personService.getClientByEmail(person.getEmail()).isPresent()) {
            errors.rejectValue("email", "error.email", "Пользователь с таким email уже существует");
        }
        if (personService.getClientByPhoneNumber(person.getPhoneNumber()).isPresent()) {
            errors.rejectValue("phoneNumber", "error.phoneNumber", "Пользователь с таким номером телефона уже существует");
        }
//        if (person.getAddresses() != null && !person.getAddresses().isEmpty()) {
//            for (String newAddress : person.getAddresses()) {
//                if (person.getAddresses().stream().filter(addr -> addr.equals(newAddress)).count() > 1) {
//                    errors.rejectValue("addresses", "error.addresses", "Дубликат адреса: " + newAddress);
//                    break;
//                }
//            }

        if (!(person.getAddress().startsWith("V") || person.getAddress().startsWith("H"))) {
            errors.rejectValue("address", "error.address", "Неверный адрес");
        } else {
            String numberPart = person.getAddress().substring(1);
            try {
                int number = Integer.parseInt(numberPart);
                if (number < 0 || number > 4000) {
                    errors.rejectValue("address", "error.address", "Неверный адрес");
                }
            } catch (NumberFormatException e) {
                errors.rejectValue("address", "error.address", "Неверный адрес");
            }
        }
    }
}