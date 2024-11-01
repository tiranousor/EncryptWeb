package com.example.WaterDelivery.util;

import com.example.WaterDelivery.providers.WaterBottle;
import com.example.WaterDelivery.services.WaterBottleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;

import static com.example.WaterDelivery.services.URLService.checkURL;


@Component
public class WaterBottleValidator implements Validator {

    private final WaterBottleService waterBottleService;

    @Autowired
    public WaterBottleValidator(WaterBottleService waterBottleService) {
        this.waterBottleService = waterBottleService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return WaterBottle.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        WaterBottle waterBottle = (WaterBottle) target;

        // Проверка корректности sizeLiters (целое число)
        if (waterBottle.getSizeLiters() == null) {
            errors.rejectValue("sizeLiters", "", "Размер бутылки не должен быть пустым");
        } else if (waterBottle.getSizeLiters() <= 0) {
            errors.rejectValue("sizeLiters", "", "Размер бутылки должен быть положительным числом");
        }

        // Проверка корректности price (число с плавающей точкой)
        if (waterBottle.getPrice() == null) {
            errors.rejectValue("price", "", "Цена не должна быть пустой");
        } else if (waterBottle.getPrice() <= 0) {
            errors.rejectValue("price", "", "Цена должна быть положительной");
        }


    }
}
