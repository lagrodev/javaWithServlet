package ru.hexaend.util.impl;

import ru.hexaend.util.PhoneValidator;

import java.util.regex.Pattern;

import static ru.hexaend.domain.ContactConstrains.MIN_NUMBERS_IN_PHONE;

public class PhoneValidatorImpl implements PhoneValidator {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[\\d\\s\\-().]{7,20}$");


    @Override
    public boolean isValid(String phone) {
        if (phone == null || phone.isBlank())
        {
            return false;
        }
        String trimmed = phone.trim();
        if (!PHONE_PATTERN.matcher(trimmed).matches())
        {
            return false;
        }

        long digitsCount = trimmed.chars()
                .filter(Character::isDigit)
                .count();
        return digitsCount >= MIN_NUMBERS_IN_PHONE;
    }
}
