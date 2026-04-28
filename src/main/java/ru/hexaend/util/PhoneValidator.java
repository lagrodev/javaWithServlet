package ru.hexaend.util;

/**
 * Валидатор телефонных номеров.
 *
 * @author Vasily Melnik
 */
public interface PhoneValidator {

    /**
     * Проверяет, соответствует ли номер телефона требованиям формата.
     *
     * @param phone номер телефона для проверки
     * @return {@code true}, если номер валиден
     */
    boolean isValid(String phone);
}
