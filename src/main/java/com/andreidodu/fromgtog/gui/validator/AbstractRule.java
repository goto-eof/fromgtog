package com.andreidodu.fromgtog.gui.validator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public abstract class AbstractRule {

    private final JSONObject json;

    public AbstractRule(JSONObject json) {
        this.json = json;
    }

    protected JSONObject getJson() {
        return json;
    }

    public abstract String getInvalidMessage();

    public boolean pass(JSONObject json) {

        if (json.optJSONArray(getKey()) != null) {
            JSONArray array = json.getJSONArray(getKey());
            return IntStream.range(0, array.length())
                    .mapToObj(array::getString)
                    .anyMatch(value -> !getPattern().matcher(value).matches());
        }


        String value = json.get(getKey()).toString();
        Matcher matcher = getPattern().matcher(value);
        return matcher.matches();
    }

    protected abstract String getKey();

    protected abstract Pattern getPattern();

}
