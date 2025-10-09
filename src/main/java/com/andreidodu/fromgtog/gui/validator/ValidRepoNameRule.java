package com.andreidodu.fromgtog.gui.validator;

import com.andreidodu.fromgtog.constants.ApplicationConstants;
import com.andreidodu.fromgtog.gui.util.RegexUtil;
import com.andreidodu.fromgtog.type.EngineOptionsType;
import com.andreidodu.fromgtog.type.EngineType;
import com.andreidodu.fromgtog.util.StringUtil;
import org.json.JSONObject;

import java.util.List;
import java.util.regex.Pattern;

import static com.andreidodu.fromgtog.gui.controller.constants.GuiKeys.*;

public class ValidRepoNameRule extends AbstractRule {
    private static final Pattern PATTERN = RegexUtil.REGEX_PATTERN_REPO_NAME;
    private static final String INVALID_MESSAGE = "Invalid 'repo name'. Valid pattern is: " + PATTERN;
    private static final List<String> REPO_NAME_KEY_LIST = List.of(
            FROM_GITEA_EXCLUDE_REPO_NAME_LIST,
            FROM_GITHUB_EXCLUDE_REPO_NAME_LIST,
            FROM_GITLAB_EXCLUDE_REPO_NAME_LIST
    );
    private static final List<String> OPTIONS_KEY_LIST = List.of(
            FROM_GITEA_OPTIONS_TABBED_PANE_INDEX,
            FROM_GITLAB_OPTIONS_TABBED_PANE_INDEX,
            FROM_GITHUB_OPTIONS_TABBED_PANE_INDEX
    );

    public ValidRepoNameRule(JSONObject json) {
        super(json);
    }

    @Override
    public boolean pass() {
        if (!List.of(EngineType.GITHUB, EngineType.GITEA, EngineType.GITLAB).contains(EngineType.fromValue(getJson().getInt(FROM_TAB_INDEX)))) {
            return true;
        }

        String optionsTabbedPaneKey = super.getKey(OPTIONS_KEY_LIST);

        validateOptionsTabbedPaneKey(optionsTabbedPaneKey);

        if (isNotFilterTab(optionsTabbedPaneKey)) {
            return true;
        }

        return super.pass();
    }

    private void validateOptionsTabbedPaneKey(String optionsTabbedPaneKey) {
        if (getJson().isNull(optionsTabbedPaneKey)) {
            throw new IllegalArgumentException("Invalid selected option, please contact the developer (:");
        }
    }

    private boolean isNotFilterTab(final String optionsKey) {
        return EngineOptionsType.FILTER != EngineOptionsType.fromValue(getJson().getInt(optionsKey));
    }

    protected String getKey() {
        return REPO_NAME_KEY_LIST.stream()
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
