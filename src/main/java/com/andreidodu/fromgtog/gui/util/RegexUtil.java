package com.andreidodu.fromgtog.gui.util;

public class RegexUtil {

    public final static String REGEX_REPO_NAME = "^[\\w\\.\\-]+$";
    public final static String REGEX_USERNAME = "^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){3,18}[a-zA-Z0-9]$";
    public final static String REGEX_NUMBER_0_120 = "^[1-9][0-9]?$|^100$";
}
