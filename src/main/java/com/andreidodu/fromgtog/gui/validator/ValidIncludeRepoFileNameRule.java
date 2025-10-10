package com.andreidodu.fromgtog.gui.validator;

import com.andreidodu.fromgtog.gui.util.RegexUtil;
import com.andreidodu.fromgtog.type.EngineOptionsType;
import com.andreidodu.fromgtog.type.EngineType;
import org.json.JSONObject;

import java.util.List;
import java.util.regex.Pattern;

import static com.andreidodu.fromgtog.gui.controller.constants.GuiKeys.*;

public class ValidIncludeRepoFileNameRule extends AbstractRule {
    private static final Pattern PATTERN = RegexUtil.REGEX_PATTERN_FILE_PATH;
    private static final String INVALID_MESSAGE = "Invalid 'filename' value. Valid pattern is: " + PATTERN + ". Current value: '%s'";
    private static final List<String> ORGANIZATION_KEY_LIST = List.of(
            FROM_GITEA_INCLUDE_REPO_NAMES_LIST_FILE,
            FROM_GITHUB_INCLUDE_REPO_NAMES_LIST_FILE,
            FROM_GITLAB_INCLUDE_REPO_NAMES_LIST_FILE
    );
    private static final List<String> OPTIONS_KEY_LIST = List.of(
            FROM_GITEA_OPTIONS_TABBED_PANE_INDEX,
            FROM_GITLAB_OPTIONS_TABBED_PANE_INDEX,
            FROM_GITHUB_OPTIONS_TABBED_PANE_INDEX
    );

    public ValidIncludeRepoFileNameRule(JSONObject json) {
        super(json);
    }

    @Override
    public boolean isApplicable() {
        List<EngineType> validTabList = List.of(EngineType.GITHUB, EngineType.GITEA, EngineType.GITLAB);
        if (!validTabList.contains(EngineType.fromValue(getJson().getInt(FROM_TAB_INDEX)))) {
            return false;
        }

        String optionsTabbedPaneKey = super.getFirstExistingKeyInList(OPTIONS_KEY_LIST);

        validateOptionsTabbedPaneKey(optionsTabbedPaneKey);

        if (!isFileTab(optionsTabbedPaneKey)) {
            return false;
        }

        return true;
    }

    private void validateOptionsTabbedPaneKey(String optionsTabbedPaneKey) {
        if (getJson().isNull(optionsTabbedPaneKey)) {
            throw new IllegalArgumentException("Invalid selected option, please contact the developer (:");
        }
    }

    private boolean isFileTab(final String optionsKey) {
        return EngineOptionsType.FILE == EngineOptionsType.fromValue(getJson().getInt(optionsKey));
    }

    @Override
    protected List<String> getKeyList() {
        return List.of(super.getFirstExistingKeyInList(ORGANIZATION_KEY_LIST));
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
