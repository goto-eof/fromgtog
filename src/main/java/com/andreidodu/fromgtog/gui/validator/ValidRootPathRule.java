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
    private static final String INVALID_MESSAGE = "Invalid 'path'. Valid pattern is: " + PATTERN;
    private final List<String> FROM_KEY_LIST = List.of(FROM_LOCAL_ROOT_PATH);
    private final List<String> TO_KEY_LIST = List.of(TO_LOCAL_ROOT_PATH);

    public ValidRootPathRule(JSONObject json) {
        super(json);
    }

    protected List<String> getKey() {
        return Stream
                .concat(
                        TO_KEY_LIST.stream().filter(key -> super.getJson().keySet().contains(key)),
                        FROM_KEY_LIST.stream().filter(key -> super.getJson().keySet().contains(key))
                )
                .toList();
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
    protected List<String> getValue() {
        return getKey().stream().map(key -> super.getJson().get(key).toString()).toList();
    }

}
