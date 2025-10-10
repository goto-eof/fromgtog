package com.andreidodu.fromgtog.gui.validator;

import lombok.Getter;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public abstract class AbstractRule {

    private final JSONObject json;

    @Getter
    private final List<String> invalidValuesList = new ArrayList<>();

    @Getter
    private final List<String> errorMessageList = new ArrayList<>();

    public AbstractRule(JSONObject json) {
        this.json = json;
    }

    protected JSONObject getJson() {
        return json;
    }

    public abstract boolean isApplicable();

    public abstract String getInvalidMessage();

    public boolean isValid() {
        List<String> invalidValues = getValueList().stream()
                .filter(this::isInvalid)
                .toList();
        invalidValuesList.addAll(invalidValues);
        invalidValues.forEach(invalidValue -> getErrorMessageList().add(String.format(getInvalidMessage(), invalidValue)));
        return invalidValuesList.isEmpty();
    }

    private boolean isInvalid(String val) {
        return val == null || val.trim().isEmpty() || !getPattern()
                .matcher(val)
                .matches();
    }

    protected abstract List<String> getValueList();

    protected abstract List<String> getKeyList();

    protected abstract Pattern getPattern();


    protected String getFirstExistingKeyInList(List<String> keyList) {
        return keyList.stream()
                .filter(key -> getJson().keySet().contains(key))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Internal problem: invalid key. Please ask the developer for a fix (:"));
    }

}
