package com.andreidodu.fromgtog.gui.validator.composite;

import org.json.JSONObject;

import java.util.List;

public interface ComponentValidator {

    boolean isApplicable(JSONObject jsonObject);

    List<String> getErrorMessageIfNotValid(JSONObject jsonObject);

}
