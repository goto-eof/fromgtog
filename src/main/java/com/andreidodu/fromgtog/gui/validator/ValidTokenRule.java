package com.andreidodu.fromgtog.gui.validator;

import com.andreidodu.fromgtog.gui.util.RegexUtil;
import com.andreidodu.fromgtog.type.EngineType;
import org.json.JSONObject;

import java.util.List;
import java.util.regex.Pattern;

import static com.andreidodu.fromgtog.gui.controller.constants.GuiKeys.*;

public class ValidTokenRule extends AbstractRule {
    private static final Pattern PATTERN = RegexUtil.REGEX_PATTERN_AT_LEAST_ONE_CHAR;
    private static final String INVALID_MESSAGE = "Invalid 'token' value. Valid pattern is: " + PATTERN + ". Current value: '%s'";
    private static final List<String> KEY_LIST = List.of(
            FROM_GITEA_TOKEN,
            FROM_GITHUB_TOKEN,
            FROM_GITLAB_TOKEN
    );

    public ValidTokenRule(JSONObject json) {
        super(json);
    }

    @Override
    public boolean isApplicable() {
        return List.of(EngineType.GITHUB, EngineType.GITEA, EngineType.GITLAB)
                .contains(EngineType.fromValue(getJson().getInt(FROM_TAB_INDEX)));
    }

    protected List<String> getKeyList() {
        return List.of(super.getFirstExistingKeyInList(KEY_LIST));
    }

    protected Pattern getPattern() {
        return PATTERN;
    }

    public String getInvalidMessage() {
        return INVALID_MESSAGE;
    }

    @Override
    protected List<String> getValueList() {
        return getKeyList().stream()
                .map(key -> getJson().get(key).toString())
                .toList();
    }

}
