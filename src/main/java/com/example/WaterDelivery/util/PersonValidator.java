package com.example.WaterDelivery.util;

import com.example.WaterDelivery.providers.Person;
import com.example.WaterDelivery.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PersonValidator implements Validator {
    private final PersonService personService;
    private static final String ADDRESS_PATTERN = "^(?i)[HV](\\d{1,3}|1000)\\s(\\d{1,4})$";
    private final Pattern pattern = Pattern.compile(ADDRESS_PATTERN);
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

        if (person.getAddress() == null || person.getAddress().trim().isEmpty()) {
            errors.rejectValue("address", "error.address", "Адрес не должен быть пустым");
            return;
        }

        Matcher matcher = pattern.matcher(person.getAddress().trim());

        if (!matcher.matches()) {
            errors.rejectValue("address", "error.address", "Неверный формат адреса");
            return;
        }

        // Извлечение чисел из адреса
        int streetNumber = Integer.parseInt(matcher.group(1));
        int houseNumber = Integer.parseInt(matcher.group(2));

        // Проверка диапазона чисел
        if (streetNumber < 1 || streetNumber > 1000) {
            errors.rejectValue("address", "error.address", "Номер улицы должен быть от 1 до 1000");
            return;
        }

        if (houseNumber < 1 || houseNumber > 4000) {
            errors.rejectValue("address", "error.address", "Номер дома должен быть от 1 до 4000");
            return;
        }

        // Дополнительные условия
        if (streetNumber == 1 && houseNumber % 2 != 0) {
            errors.rejectValue("address", "error.address",
                    "Если номер улицы равен 1, номер дома должен быть чётным");
            return;
        }

        if (streetNumber == 1000 && houseNumber % 2 == 0) {
            errors.rejectValue("address", "error.address",
                    "Если номер улицы равен 1000, номер дома должен быть нечётным");
        }
    }
}