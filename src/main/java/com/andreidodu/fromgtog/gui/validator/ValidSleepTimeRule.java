package com.andreidodu.fromgtog.gui.validator;

import com.andreidodu.fromgtog.gui.util.RegexUtil;
import org.json.JSONObject;

import java.util.regex.Pattern;

import static com.andreidodu.fromgtog.gui.controller.constants.GuiKeys.APP_SLEEP_TIME;

public class ValidSleepTimeRule extends AbstractRule {
    private static final Pattern PATTERN = RegexUtil.REGEX_PATTERN_NUMBER_0_120;
    private static final String INVALID_MESSAGE = "Invalid 'sleep time'. Valid pattern is: " + PATTERN;
    private static final String KEY = APP_SLEEP_TIME;

    public ValidSleepTimeRule(JSONObject json) {
        super(json);
    }

    protected String getKey() {
        return KEY;
    }

    protected Pattern getPattern() {
        return PATTERN;
    }

    public String getInvalidMessage() {
        return INVALID_MESSAGE;
    }

    @Override
    protected String getValue() {
        return super.getJson().get(getKey()).toString();
    }

}
