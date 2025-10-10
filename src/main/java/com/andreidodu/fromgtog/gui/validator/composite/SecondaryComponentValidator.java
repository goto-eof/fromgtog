package com.andreidodu.fromgtog.gui.validator.composite;

import lombok.Getter;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class SecondaryComponentValidator implements ComponentValidator {

    @Getter
    private final Predicate<JSONObject> isApplicablePredicate;

    @Getter
    private final Predicate<JSONObject> isValidPredicate;

    @Getter
    private final String fieldName;

    public SecondaryComponentValidator(Predicate<JSONObject> isApplicablePredicate, Predicate<JSONObject> isValidPredicate, String fieldName) {
        this.isApplicablePredicate = isApplicablePredicate;
        this.isValidPredicate = isValidPredicate;
        this.fieldName = fieldName;
    }

    @Override
    public boolean isApplicable(JSONObject jsonObject) {
        return this.getIsApplicablePredicate().test(jsonObject);
    }

    @Override
    public List<String> getErrorMessageIfNotValid(JSONObject jsonObject) {
        return this.getIsValidPredicate().test(jsonObject) ? Collections.emptyList() : List.of(String.format("Error: %s", getFieldName()));
    }

}
