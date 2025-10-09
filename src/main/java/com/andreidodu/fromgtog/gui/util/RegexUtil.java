package com.andreidodu.fromgtog.gui.util;

import java.util.regex.Pattern;

public class RegexUtil {

    private final static String REGEX_REPO_NAME = "^[\\w\\.\\-]+$";
    private final static String REGEX_USERNAME = "^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){3,18}[a-zA-Z0-9]$";
    private final static String REGEX_NUMBER_0_120 = "^[0-9][0-9]?$|^100$";
    private final static String REGEX_AT_LEAST_ONE_CHAR = "^([0-9a-zA-Z_]+)$";

    public final static Pattern REGEX_PATTERN_REPO_NAME = Pattern.compile(REGEX_REPO_NAME);
    public final static Pattern REGEX_PATTERN_USERNAME = Pattern.compile(REGEX_USERNAME);
    public final static Pattern REGEX_PATTERN_NUMBER_0_120 = Pattern.compile(REGEX_NUMBER_0_120);
    public final static Pattern REGEX_PATTERN_AT_LEAST_ONE_CHAR = Pattern.compile(REGEX_AT_LEAST_ONE_CHAR);
}
