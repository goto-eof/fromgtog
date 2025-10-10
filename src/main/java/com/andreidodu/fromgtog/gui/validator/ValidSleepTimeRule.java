package com.andreidodu.fromgtog.gui.validator;

import com.andreidodu.fromgtog.gui.util.RegexUtil;
import org.json.JSONObject;

import java.util.List;
import java.util.regex.Pattern;

import static com.andreidodu.fromgtog.gui.controller.constants.GuiKeys.APP_SLEEP_TIME;

public class ValidSleepTimeRule extends AbstractRule {
    private static final Pattern PATTERN = RegexUtil.REGEX_PATTERN_NUMBER_0_120;
    private static final String FIELD_NAME = "sleep time";
    private static final String KEY = APP_SLEEP_TIME;

    public ValidSleepTimeRule(JSONObject json) {
        super(json);
    }

    @Override
    public boolean isApplicable() {
        return true;
    }

    protected List<String> getKeyList() {
        return List.of(KEY);
    }

    protected Pattern getPattern() {
        return PATTERN;
    }

    public String getFieldName() {
        return FIELD_NAME;
    }

    @Override
    protected List<String> getValueList() {
        return getKeyList().stream()
                .map(key -> super.getJson().get(key).toString())
                .toList();
    }

}
