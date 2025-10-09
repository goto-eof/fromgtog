package com.andreidodu.fromgtog.gui.validator;

import com.andreidodu.fromgtog.constants.ApplicationConstants;
import com.andreidodu.fromgtog.gui.util.RegexUtil;
import com.andreidodu.fromgtog.util.StringUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.regex.Pattern;

import static com.andreidodu.fromgtog.gui.controller.constants.GuiKeys.*;

public class ValidRepoNameRule extends AbstractRule {
    private static final Pattern PATTERN = RegexUtil.REGEX_PATTERN_REPO_NAME;
    private static final String INVALID_MESSAGE = "Invalid 'repo name'. Valid pattern is: " + PATTERN;
    private static final List<String> KEY_LIST = List.of(
            FROM_GITEA_EXCLUDE_REPO_NAME_LIST,
            FROM_GITHUB_EXCLUDE_REPO_NAME_LIST,
            FROM_GITLAB_EXCLUDE_REPO_NAME_LIST
    );

    public ValidRepoNameRule(JSONObject json) {
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
    protected List<String> getValue() {
        return StringUtil.stringsSeparatedByCommaToList(getJson().getString(getKey()), ApplicationConstants.LIST_ITEM_SEPARATOR);
    }

}
