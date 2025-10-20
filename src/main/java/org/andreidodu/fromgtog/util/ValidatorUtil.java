package org.andreidodu.fromgtog.util;

import java.util.Optional;

public class ValidatorUtil {

    public static void validateIsNotNullAndNotBlank(String value) {
        validateIsNotNull(value);
        validateIsNotBlank(value);
    }


    public static void validateIsNotNullAndNotBlank(Optional<String> optionalValue) {
        validateIsNotNull(optionalValue);
        validateIsNotBlank(optionalValue);
    }

    public static void validateIsNotBlank(String value) {
        if (value.isBlank()) {
            throw new IllegalArgumentException("blank value not allowed");
        }
    }

    public static void validateIsNotBlank(Optional<String> optionalValue) {
        String value = optionalValue.orElseThrow(() -> new IllegalArgumentException("value not present."));
        if (value.isBlank()) {
            throw new IllegalArgumentException("blank optionalValue not allowed");
        }
    }

    public static <T> void validateIsNotNull(T value) {
        if (value == null) {
            throw new IllegalArgumentException("null value not allowed");
        }
    }
}
