package com.andreidodu.fromgtog.gui.util;

import java.util.regex.Pattern;

public class RegexUtil {

    private final static String REGEX_FILE_PATH = "^/([^/]+)(/[^/]+)*$";
    public static final Pattern REGEX_PATTERN_WINDOWS_FILE_PATH = Pattern.compile("^[a-zA-Z]:[\\\\\\/](?:[^\\\\\\/:\\*\\?\"<>|]+[\\\\\\/])*[^\\\\\\/:\\*\\?\"<>|]*$");
    public static final Pattern REGEX_PATTERN_MAC_FILE_PATH = Pattern.compile(REGEX_FILE_PATH);
    public final static Pattern REGEX_PATTERN_LINUX_FILE_PATH = Pattern.compile(REGEX_FILE_PATH);

    private final static String REGEX_REPO_NAME = "^[a-zA-Z0-9._-]+$";
    public final static Pattern REGEX_PATTERN_REPO_NAME = Pattern.compile(REGEX_REPO_NAME);

    private final static String REGEX_USERNAME = "^[a-zA-Z0-9]([a-zA-Z0-9]|[._-][a-zA-Z0-9]){3,18}[a-zA-Z0-9]$";
    public final static Pattern REGEX_PATTERN_USERNAME = Pattern.compile(REGEX_USERNAME);

    private final static String REGEX_NUMBER_0_120 = "^(?:[1-9][0-9]?|1[01][0-9]|120|0)$";
    public final static Pattern REGEX_PATTERN_NUMBER_0_120 = Pattern.compile(REGEX_NUMBER_0_120);

    private final static String REGEX_AT_LEAST_ONE_CHAR = "^[0-9a-zA-Z_-]+$";
    public final static Pattern REGEX_PATTERN_AT_LEAST_ONE_CHAR = Pattern.compile(REGEX_AT_LEAST_ONE_CHAR);

    private final static String REGEX_LINUX_MAC_DIR_PATH = "^/|(/[a-zA-Z0-9_-]+)+$";
    public final static Pattern REGEX_PATTERN_LINUX_DIR_PATH = Pattern.compile(REGEX_LINUX_MAC_DIR_PATH);
    public final static Pattern REGEX_PATTERN_MAC_DIR_PATH = Pattern.compile(REGEX_LINUX_MAC_DIR_PATH);
    public final static Pattern REGEX_PATTERN_WINDOWS_DIR_PATH = Pattern.compile("^[a-zA-Z]:[\\\\\\/](?:[^\\\\\\/:\\*\\?\"<>|]+[\\\\\\/])*$");

    private final static String REGEX_URL = "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)";
    public final static Pattern REGEX_PATTERN_URL = Pattern.compile(REGEX_URL);
}
