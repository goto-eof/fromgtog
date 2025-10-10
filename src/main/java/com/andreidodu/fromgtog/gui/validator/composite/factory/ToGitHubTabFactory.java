package com.andreidodu.fromgtog.gui.validator.composite.factory;

import com.andreidodu.fromgtog.gui.validator.composite.ComponentValidator;
import com.andreidodu.fromgtog.gui.validator.composite.PrimaryComponentValidator;
import com.andreidodu.fromgtog.gui.validator.composite.SecondaryComponentValidator;
import com.andreidodu.fromgtog.type.EngineType;
import org.json.JSONObject;

import java.util.function.Predicate;

import static com.andreidodu.fromgtog.gui.controller.constants.GuiKeys.TO_GITHUB_TOKEN;
import static com.andreidodu.fromgtog.gui.validator.composite.factory.util.FactoryUtil.isMandatoryTokenValid;
import static com.andreidodu.fromgtog.gui.validator.composite.factory.util.FactoryUtil.isToMainTabIndexEqualTo;

public class ToGitHubTabFactory implements ComponentValidatorFactory {
    @Override
    public ComponentValidator createValidator() {
        Predicate<JSONObject> mainTabPredicate = isToMainTabIndexEqualTo(EngineType.GITHUB);

        PrimaryComponentValidator primaryTab = new PrimaryComponentValidator(mainTabPredicate);

        primaryTab.addComponentValidator(new SecondaryComponentValidator(mainTabPredicate, isMandatoryTokenValid(TO_GITHUB_TOKEN), "token"));

        return primaryTab;
    }


}
