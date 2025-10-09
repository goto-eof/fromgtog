package com.andreidodu.fromgtog.gui.validator;

import lombok.Getter;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractRule {

    private final JSONObject json;

    @Getter
    private final List<String> invalidValuesList = new ArrayList<>();

    public AbstractRule(JSONObject json) {
        this.json = json;
    }

    protected JSONObject getJson() {
        return json;
    }

    public abstract boolean isApplicable();

    public abstract String getInvalidMessage();

    public boolean pass() {

        var value = this.getValue();

        if (value instanceof List list) {
            List<String> stringList = (List<String>) value;
            invalidValuesList.addAll(stringList.stream()
                    .filter(
                            val -> !getPattern()
                                    .matcher(val)
                                    .matches()
                    )
                    .toList());
            return invalidValuesList.isEmpty();
        }

        if (value instanceof String valueString) {
            Matcher matcher = getPattern().matcher(valueString);
            final boolean pass = matcher.matches();
            if (!pass) {
                invalidValuesList.add(valueString);
            }
            return pass;
        }

        throw new IllegalArgumentException("Internal problem: invalid value. Please ask the developer for a fix (:");
    }

    protected abstract <T> T getValue();

    protected abstract String getKey();

    protected abstract Pattern getPattern();


    protected String getKey(List<String> keyList) {
        return keyList.stream()
                .filter(key -> getJson().keySet().contains(key))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Internal problem: invalid key. Please ask the developer for a fix (:"));
    }

}
