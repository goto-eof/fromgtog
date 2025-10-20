package org.andreidodu.fromgtog.util;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class StringUtil {

    public static List<String> stringsSeparatedByCommaToList(final String stringsSeparatedByComma, final String separator) {
        Objects.requireNonNull(stringsSeparatedByComma);
        Objects.requireNonNull(separator);

        return Arrays.stream(stringsSeparatedByComma.trim().split(separator))
                .map(String::toLowerCase)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .toList();
    }

    public static String[] stringSeparatedByCommaToArray(final String stringsSeparatedByComma, final String separator) {
        Objects.requireNonNull(stringsSeparatedByComma);
        Objects.requireNonNull(separator);

        return stringsSeparatedByCommaToList(stringsSeparatedByComma, separator)
                .toArray(String[]::new);
    }

}
