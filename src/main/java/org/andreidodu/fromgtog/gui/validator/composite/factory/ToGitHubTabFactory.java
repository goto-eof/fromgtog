package org.andreidodu.fromgtog.gui.validator.composite.factory;

import org.andreidodu.fromgtog.gui.validator.composite.ComponentValidator;
import org.andreidodu.fromgtog.gui.validator.composite.PrimaryComponentValidator;
import org.andreidodu.fromgtog.gui.validator.composite.SecondaryComponentValidator;
import org.andreidodu.fromgtog.type.EngineType;
import org.json.JSONObject;

import java.util.function.Predicate;

import static org.andreidodu.fromgtog.gui.controller.constants.GuiKeys.TO_GITHUB_TOKEN;
import static org.andreidodu.fromgtog.gui.validator.composite.factory.util.FactoryUtil.isMandatoryTokenValid;
import static org.andreidodu.fromgtog.gui.validator.composite.factory.util.FactoryUtil.isToMainTabIndexEqualTo;

public class ToGitHubTabFactory implements ComponentValidatorFactory {
    @Override
    public ComponentValidator createValidator() {
        Predicate<JSONObject> mainTabPredicate = isToMainTabIndexEqualTo(EngineType.GITHUB);

        PrimaryComponentValidator primaryTab = new PrimaryComponentValidator(mainTabPredicate);

        primaryTab.addComponentValidator(new SecondaryComponentValidator(mainTabPredicate, isMandatoryTokenValid(TO_GITHUB_TOKEN), "token"));

        return primaryTab;
    }


}
