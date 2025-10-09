package com.andreidodu.fromgtog.gui.validator;

import com.andreidodu.fromgtog.gui.util.RegexUtil;
import com.andreidodu.fromgtog.type.EngineType;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.andreidodu.fromgtog.gui.controller.constants.GuiKeys.*;

public class ValidRootPathRule extends AbstractRule {
    private static final Pattern PATTERN = RegexUtil.REGEX_PATTERN_DIR_PATH;
    private static final String INVALID_MESSAGE = "Invalid 'path'. Valid pattern is: " + PATTERN;
    private final List<String> KEY_LIST = List.of(
            FROM_LOCAL_ROOT_PATH,
            TO_LOCAL_ROOT_PATH
    );

    public ValidRootPathRule(JSONObject json) {
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

    @Override
    public boolean isApplicable() {
        return Objects.equals(EngineType.LOCAL, EngineType.fromValue(getJson().getInt(FROM_TAB_INDEX)));
    }

    @Override
    public boolean isValid() {
        if (getValue() == null || getValue().isEmpty()) {
            return false;
        }
        return super.isValid();
    }

    public String getInvalidMessage() {
        return INVALID_MESSAGE;
    }

    @Override
    protected String getValue() {
        return super.getJson().get(getKey()).toString();
    }

}
