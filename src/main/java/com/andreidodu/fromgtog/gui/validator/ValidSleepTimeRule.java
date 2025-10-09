package com.andreidodu.fromgtog.gui.validator;

import com.andreidodu.fromgtog.gui.util.RegexUtil;

import java.util.regex.Pattern;

import static com.andreidodu.fromgtog.gui.controller.constants.GuiKeys.APP_SLEEP_TIME;

public class ValidSleepTimeRule extends AbstractRule {
    public static final String REGEX = RegexUtil.REGEX_NUMBER_0_120;
    public static final String INVALID_MESSAGE = "Invalid 'sleep time'. Valid pattern is: " + REGEX;
    public static final String KEY = APP_SLEEP_TIME;

    public final static Pattern PATTERN = Pattern.compile(REGEX);

    protected String getKey() {
        return KEY;
    }

    protected Pattern getPattern() {
        return PATTERN;
    }

    public String getInvalidMessage() {
        return INVALID_MESSAGE;
    }

}
