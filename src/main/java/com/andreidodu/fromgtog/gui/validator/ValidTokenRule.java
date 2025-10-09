package com.andreidodu.fromgtog.gui.validator;

import com.andreidodu.fromgtog.constants.ApplicationConstants;
import com.andreidodu.fromgtog.gui.util.RegexUtil;
import com.andreidodu.fromgtog.util.StringUtil;
import org.json.JSONObject;

import java.util.List;
import java.util.regex.Pattern;

import static com.andreidodu.fromgtog.gui.controller.constants.GuiKeys.*;

public class ValidTokenRule extends AbstractRule {
    private static final Pattern PATTERN = RegexUtil.REGEX_PATTERN_AT_LEAST_ONE_CHAR;
    private static final String INVALID_MESSAGE = "Invalid 'token'. Valid pattern is: " + PATTERN;
    private static final List<String> KEY_LIST = List.of(
            FROM_GITEA_TOKEN,
            FROM_GITHUB_TOKEN,
            FROM_GITLAB_TOKEN
    );

    public ValidTokenRule(JSONObject json) {
        super(json);
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
