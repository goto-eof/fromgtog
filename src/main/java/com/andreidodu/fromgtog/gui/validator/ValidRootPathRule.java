package com.andreidodu.fromgtog.gui.validator;

import com.andreidodu.fromgtog.gui.util.RegexUtil;
import com.andreidodu.fromgtog.type.EngineType;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.andreidodu.fromgtog.gui.controller.constants.GuiKeys.*;

public class ValidRootPathRule extends AbstractRule {
    private static final Pattern PATTERN = RegexUtil.REGEX_PATTERN_DIR_PATH;
    private static final String FIELD_NAME = "path";
    private final List<String> KEY_LIST = List.of(FROM_LOCAL_ROOT_PATH, TO_LOCAL_ROOT_PATH);

    public ValidRootPathRule(JSONObject json) {
        super(json);
    }

    protected List<String> getKeyList() {
        return KEY_LIST;
    }

    protected Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public boolean isApplicable() {
        return Objects.equals(EngineType.LOCAL, EngineType.fromValue(getJson().getInt(FROM_TAB_INDEX))) ||
                Objects.equals(EngineType.LOCAL, EngineType.fromValue(getJson().getInt(TO_TAB_INDEX)));
    }

    @Override
    public boolean isValid() {
        if (getValueList() == null || getValueList().isEmpty()) {
            return false;
        }
        return super.isValid();
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
