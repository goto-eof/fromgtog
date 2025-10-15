package com.andreidodu.fromgtog.gui.validator.composite;

import org.json.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public record SecondaryComponentValidator(Predicate<JSONObject> isApplicablePredicate,
                                          Predicate<JSONObject> isValidPredicate,
                                          String fieldName) implements ComponentValidator {

    @Override
    public boolean isApplicable(JSONObject jsonObject) {
        return this.isApplicablePredicate().test(jsonObject);
    }

    @Override
    public List<String> getErrorMessageIfNotValid(JSONObject jsonObject) {
        return this.isValidPredicate().test(jsonObject) ? Collections.emptyList() : List.of(String.format("Invalid input: %s", fieldName()));
    }

}
