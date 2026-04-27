package ru.hexaend.util.impl;

import ru.hexaend.util.ContactConstraints;
import ru.hexaend.util.PhoneValidator;

import java.util.regex.Pattern;

import static ru.hexaend.util.ContactConstraints.MIN_NUMBERS_IN_PHONE;

/**
 * Реализация {@link PhoneValidator} на основе регулярного выражения.
 *
 * <p>Допустимый формат: опциональный {@code +}, затем цифры, пробелы, дефисы,
 * точки и скобки (от 7 до 20 символов). Дополнительно проверяется,
 * что количество цифр не менее {@value ContactConstraints#MIN_NUMBERS_IN_PHONE}.</p>
 *
 * @author Vasily Melnik
 */
public class PhoneValidatorImpl implements PhoneValidator {

    /**
     * Паттерн допустимых символов телефонного номера.
     */
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[\\d\\s\\-().]{7,20}$");

    /**
     * Проверяет номер телефона на соответствие формату и минимальному количеству цифр.
     *
     * @param phone номер для проверки
     * @return {@code true}, если номер валиден
     */
    @Override
    public boolean isValid(String phone) {
        if (phone == null || phone.isBlank()) {
            return false;
        }
        final String trimmed = phone.trim();
        if (!PHONE_PATTERN.matcher(trimmed).matches()) {
            return false;
        }

        final long digitsCount = trimmed.chars()
                .filter(Character::isDigit)
                .count();
        return digitsCount >= MIN_NUMBERS_IN_PHONE;
    }
}
