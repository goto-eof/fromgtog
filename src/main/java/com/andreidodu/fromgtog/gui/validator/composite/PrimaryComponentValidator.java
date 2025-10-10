package com.andreidodu.fromgtog.gui.validator.composite;

import lombok.Getter;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class PrimaryComponentValidator implements ComponentValidator {

    @Getter
    private final List<ComponentValidator> componentValidatorList;

    @Getter
    private final Predicate<JSONObject> isApplicablePredicate;

    public PrimaryComponentValidator(Predicate<JSONObject> isApplicablePredicate) {
        this.componentValidatorList = new ArrayList<>();
        this.isApplicablePredicate = isApplicablePredicate;
    }

    public void addComponentValidator(ComponentValidator componentValidator) {
        this.getComponentValidatorList().add(componentValidator);
    }

    public boolean removeComponentValidator(ComponentValidator componentValidator) {
        return this.getComponentValidatorList().remove(componentValidator);
    }

    @Override
    public boolean isApplicable(JSONObject jsonObject) {
        return this.getIsApplicablePredicate().test(jsonObject);
    }

    @Override
    public List<String> getErrorMessageIfNotValid(JSONObject jsonObject) {
        return getComponentValidatorList()
                .stream()
                .map(componentValidator -> componentValidator.getErrorMessageIfNotValid(jsonObject))
                .flatMap(List::stream)
                .toList();
    }
}
