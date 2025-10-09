package com.andreidodu.fromgtog.gui.validator;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractRule {

    public abstract String getInvalidMessage();

    public boolean pass(JSONObject json) {
        String value = json.get(getKey()).toString();
        Matcher matcher = getPattern().matcher(value);
        return matcher.matches();
    }

    protected abstract String getKey();

    protected abstract Pattern getPattern();

}
