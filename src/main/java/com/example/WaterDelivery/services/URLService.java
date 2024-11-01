package com.example.WaterDelivery.services;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Сервис для проверки корректности URL.
 */
public class URLService {
    /**
     * Проверяет, является ли строка допустимым URL.
     *
     * @param url Строка URL для проверки.
     * @return true, если URL корректен, иначе false.
     */
    public static boolean checkURL(String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
