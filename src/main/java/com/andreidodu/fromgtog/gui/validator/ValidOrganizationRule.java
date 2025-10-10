package com.andreidodu.fromgtog.gui.validator;

import com.andreidodu.fromgtog.constants.ApplicationConstants;
import com.andreidodu.fromgtog.gui.util.RegexUtil;
import com.andreidodu.fromgtog.type.EngineOptionsType;
import com.andreidodu.fromgtog.type.EngineType;
import com.andreidodu.fromgtog.util.StringUtil;
import org.json.JSONObject;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import static com.andreidodu.fromgtog.gui.controller.constants.GuiKeys.*;

public class ValidOrganizationRule extends AbstractRule {
    private static final Pattern PATTERN = RegexUtil.REGEX_PATTERN_USERNAME;
    private static final String FIELD_NAME = "organization name";
    private static final List<String> ORGANIZATION_KEY_LIST = List.of(
            FROM_GITEA_EXCLUDE_ORGANIZATIONS,
            FROM_GITHUB_EXCLUDE_ORGANIZATIONS,
            FROM_GITLAB_EXCLUDE_ORGANIZATIONS
    );
    private static final List<String> OPTIONS_KEY_LIST = List.of(
            FROM_GITEA_OPTIONS_TABBED_PANE_INDEX,
            FROM_GITLAB_OPTIONS_TABBED_PANE_INDEX,
            FROM_GITHUB_OPTIONS_TABBED_PANE_INDEX
    );

    public ValidOrganizationRule(JSONObject json) {
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

        if (!isFilterTab(optionsTabbedPaneKey)) {
            return false;
        }

        return true;
    }

    private void validateOptionsTabbedPaneKey(String optionsTabbedPaneKey) {
        if (getJson().isNull(optionsTabbedPaneKey)) {
            throw new IllegalArgumentException("Invalid selected option, please contact the developer (:");
        }
    }

    private boolean isFilterTab(final String optionsKey) {
        return EngineOptionsType.FILTER == EngineOptionsType.fromValue(getJson().getInt(optionsKey));
    }

    @Override
    protected List<String> getKeyList() {
        return List.of(super.getFirstExistingKeyInList(ORGANIZATION_KEY_LIST));
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
                .map(key -> StringUtil.stringsSeparatedByCommaToList(getJson().getString(key), ApplicationConstants.LIST_ITEM_SEPARATOR))
                .flatMap(Collection::stream).toList();
    }

}
