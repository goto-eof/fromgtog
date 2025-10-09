package com.andreidodu.fromgtog.gui.validator;

import com.andreidodu.fromgtog.gui.util.RegexUtil;
import com.andreidodu.fromgtog.type.EngineType;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.andreidodu.fromgtog.gui.controller.constants.GuiKeys.*;

public class ValidRootPathRule extends AbstractRule {
    private static final Pattern PATTERN = RegexUtil.REGEX_PATTERN_DIR_PATH;
    private static final String INVALID_MESSAGE = "Invalid 'path'. Valid pattern is: " + PATTERN;

    private final String key;

    public ValidRootPathRule(JSONObject json, String key) {
        super(json);
        this.key = key;
    }

    protected String getKey() {
        return key;
    }

    protected Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public boolean isApplicable() {
        return Objects.equals(EngineType.LOCAL, EngineType.fromValue(getJson().getInt(FROM_TAB_INDEX)));
    }

    public String getInvalidMessage() {
        return INVALID_MESSAGE;
    }

    @Override
    protected String getValue() {
        return super.getJson().get(getKey()).toString();
    }

}
