package com.andreidodu.fromgtog.gui.validator;

import com.andreidodu.fromgtog.gui.util.RegexUtil;
import com.andreidodu.fromgtog.type.EngineType;
import org.json.JSONObject;

import java.util.List;
import java.util.regex.Pattern;

import static com.andreidodu.fromgtog.gui.controller.constants.GuiKeys.*;

public class ValidUrlRule extends AbstractRule {
    private static final Pattern PATTERN = RegexUtil.REGEX_PATTERN_URL;
    private static final String FIELD_NAME = "URL";
    private static final List<String> KEY_LIST = List.of(
            FROM_GITEA_URL,
            FROM_GITLAB_URL,
            TO_GITEA_URL,
            TO_GITLAB_URL
    );

    public ValidUrlRule(JSONObject json) {
        super(json);
    }

    @Override
    public boolean isApplicable() {
        List<EngineType> validEngineTypeList = List.of(EngineType.GITEA, EngineType.GITLAB);
        return validEngineTypeList.contains(EngineType.fromValue(getJson().getInt(FROM_TAB_INDEX))) ||
                validEngineTypeList.contains(EngineType.fromValue(getJson().getInt(TO_TAB_INDEX)));
    }

    protected List<String> getKeyList() {
        return KEY_LIST;
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
                .filter(key -> getJson().has(key))
                .map(key -> getJson().get(key).toString())
                .toList();
    }

}
