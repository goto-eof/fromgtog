package com.andreidodu.fromgtog.gui.validator.composite;

import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

public class ValidationComposite {

    public static List<String> getErrorMessageList(JSONObject jsonObject, List<ComponentValidator> componentValidatorList) {
        Objects.requireNonNull(jsonObject);

        return componentValidatorList.stream()
                .filter(componentValidator -> componentValidator.isApplicable(jsonObject))
                .flatMap(componentValidator -> componentValidator.getErrorMessageIfNotValid(jsonObject).stream())
                .toList();
    }

}
