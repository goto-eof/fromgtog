package com.andreidodu.fromgtog.gui.validator.composite.factory;

import com.andreidodu.fromgtog.gui.validator.composite.ComponentValidator;
import com.andreidodu.fromgtog.gui.validator.composite.PrimaryComponentValidator;
import com.andreidodu.fromgtog.gui.validator.composite.SecondaryComponentValidator;
import com.andreidodu.fromgtog.type.EngineOptionsType;
import com.andreidodu.fromgtog.type.EngineType;
import org.json.JSONObject;

import java.util.function.Predicate;

import static com.andreidodu.fromgtog.gui.controller.constants.GuiKeys.FROM_GITHUB_OPTIONS_TABBED_PANE_INDEX;
import static com.andreidodu.fromgtog.gui.controller.constants.GuiKeys.FROM_GITHUB_TOKEN;
import static com.andreidodu.fromgtog.gui.validator.composite.factory.util.FactoryUtil.*;

public class FromGitHubTabFileTabFactory implements ComponentValidatorFactory {
    @Override
    public ComponentValidator createValidator() {
        Predicate<JSONObject> mainTabPredicate = isFromMainTabIndexEqualTo(EngineType.GITHUB);
        Predicate<JSONObject> secondaryTabPredicate = isSecondaryTabIndexEqualTo(EngineOptionsType.FILE, FROM_GITHUB_OPTIONS_TABBED_PANE_INDEX);
        Predicate<JSONObject> mainIsApplicableCondition = mainTabPredicate.and(secondaryTabPredicate);

        PrimaryComponentValidator primaryTab = new PrimaryComponentValidator(mainIsApplicableCondition);

        primaryTab.addComponentValidator(new SecondaryComponentValidator(mainIsApplicableCondition, isMandatoryTokenValid(FROM_GITHUB_TOKEN), "token"));

        buildGitHubFileTab(mainIsApplicableCondition, primaryTab);

        return primaryTab;
    }


}
