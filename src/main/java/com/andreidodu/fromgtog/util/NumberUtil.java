package com.andreidodu.fromgtog.util;

public class NumberUtil {

    public static int toIntegerOrDefault(String number) {
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException e) {
            return 1;
        }
    }

}
