package org.andreidodu.fromgtog.gui.validator.composite.factory;

import org.andreidodu.fromgtog.gui.validator.composite.ComponentValidator;
import org.andreidodu.fromgtog.gui.validator.composite.PrimaryComponentValidator;
import org.andreidodu.fromgtog.gui.validator.composite.SecondaryComponentValidator;
import org.andreidodu.fromgtog.type.EngineOptionsType;
import org.andreidodu.fromgtog.type.EngineType;
import org.json.JSONObject;

import java.util.function.Predicate;

import static org.andreidodu.fromgtog.gui.controller.constants.GuiKeys.FROM_GITHUB_OPTIONS_TABBED_PANE_INDEX;
import static org.andreidodu.fromgtog.gui.controller.constants.GuiKeys.FROM_GITHUB_TOKEN;
import static org.andreidodu.fromgtog.gui.validator.composite.factory.util.FactoryUtil.*;

public class FromGitHubTabFilterTabFactory implements ComponentValidatorFactory {
    @Override
    public ComponentValidator createValidator() {
        Predicate<JSONObject> mainTabPredicate = isFromMainTabIndexEqualTo(EngineType.GITHUB);
        Predicate<JSONObject> secondaryTabPredicate = isSecondaryTabIndexEqualTo(EngineOptionsType.FILTER, FROM_GITHUB_OPTIONS_TABBED_PANE_INDEX);
        Predicate<JSONObject> mainIsApplicableCondition = mainTabPredicate.and(secondaryTabPredicate);

        PrimaryComponentValidator primaryTab = new PrimaryComponentValidator(mainIsApplicableCondition);

        primaryTab.addComponentValidator(new SecondaryComponentValidator(mainIsApplicableCondition, isMandatoryTokenValid(FROM_GITHUB_TOKEN), "token"));

        buildGitHubFilterTab(mainIsApplicableCondition, primaryTab);

        return primaryTab;
    }


}
