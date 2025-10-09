package com.andreidodu.fromgtog.gui.validator;

import com.andreidodu.fromgtog.gui.util.RegexUtil;
import org.json.JSONObject;

import java.util.List;
import java.util.regex.Pattern;

import static com.andreidodu.fromgtog.gui.controller.constants.GuiKeys.*;

public class ValidOrganizationRule extends AbstractRule {
    private static final Pattern PATTERN = RegexUtil.REGEX_PATTERN_USERNAME;
    private static final String INVALID_MESSAGE = "Invalid 'username'. Valid pattern is: " + PATTERN;
    private static final List<String> KEY_LIST = List.of(
            FROM_GITEA_EXCLUDE_ORGANIZATIONS,
            FROM_GITHUB_EXCLUDE_ORGANIZATIONS,
            FROM_GITLAB_EXCLUDE_ORGANIZATIONS
    );

    public ValidOrganizationRule(JSONObject json) {
        super(json);
    }

    protected String getKey() {
        return KEY_LIST.stream()
                .filter(key -> super.getJson().keySet().contains(key))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Internal problem: invalid key. Please ask the developer for a fix."));
    }

    protected Pattern getPattern() {
        return PATTERN;
    }

    public String getInvalidMessage() {
        return INVALID_MESSAGE;
    }

}
