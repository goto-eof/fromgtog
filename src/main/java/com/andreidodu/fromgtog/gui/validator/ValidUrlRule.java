package com.andreidodu.fromgtog.gui.validator;

import com.andreidodu.fromgtog.gui.util.RegexUtil;
import com.andreidodu.fromgtog.type.EngineType;
import org.json.JSONObject;

import java.util.List;
import java.util.regex.Pattern;

import static com.andreidodu.fromgtog.gui.controller.constants.GuiKeys.*;

public class ValidUrlRule extends AbstractRule {
    private static final Pattern PATTERN = RegexUtil.REGEX_PATTERN_URL;
    private static final String INVALID_MESSAGE = "Invalid 'url'. Valid pattern is: " + PATTERN;
    private static final List<String> KEY_LIST = List.of(
            FROM_GITEA_URL,
            FROM_GITLAB_URL
    );

    public ValidUrlRule(JSONObject json) {
        super(json);
    }

    @Override
    public boolean isApplicable() {
        return List.of(EngineType.GITEA, EngineType.GITLAB)
                .contains(EngineType.fromValue(getJson().getInt(FROM_TAB_INDEX)));
    }

    protected String getKey() {
        return KEY_LIST.stream()
                .filter(key -> super.getJson().keySet().contains(key))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Internal problem: invalid key. Please ask the developer for a fix (:"));
    }

    protected Pattern getPattern() {
        return PATTERN;
    }

    public String getInvalidMessage() {
        return INVALID_MESSAGE;
    }

    @Override
    protected String getValue() {
        return getJson().get(getKey()).toString();
    }

}
