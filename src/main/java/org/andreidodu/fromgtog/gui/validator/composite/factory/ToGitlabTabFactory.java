package org.andreidodu.fromgtog.gui.validator.composite.factory;

import org.andreidodu.fromgtog.gui.validator.composite.ComponentValidator;
import org.andreidodu.fromgtog.gui.validator.composite.PrimaryComponentValidator;
import org.andreidodu.fromgtog.gui.validator.composite.SecondaryComponentValidator;
import org.andreidodu.fromgtog.type.EngineType;
import org.json.JSONObject;

import java.util.function.Predicate;

import static org.andreidodu.fromgtog.gui.controller.constants.GuiKeys.TO_GITLAB_TOKEN;
import static org.andreidodu.fromgtog.gui.controller.constants.GuiKeys.TO_GITLAB_URL;
import static org.andreidodu.fromgtog.gui.validator.composite.factory.util.FactoryUtil.*;

public class ToGitlabTabFactory implements ComponentValidatorFactory {
    @Override
    public ComponentValidator createValidator() {
        Predicate<JSONObject> mainTabPredicate = isToMainTabIndexEqualTo(EngineType.GITLAB);
        PrimaryComponentValidator primaryTab = new PrimaryComponentValidator(mainTabPredicate);

        primaryTab.addComponentValidator(new SecondaryComponentValidator(mainTabPredicate, isMandatoryTokenValid(TO_GITLAB_TOKEN), "token"));
        primaryTab.addComponentValidator(new SecondaryComponentValidator(mainTabPredicate, isMandatoryUrlValid(TO_GITLAB_URL), "URL"));

        return primaryTab;
    }


}
