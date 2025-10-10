package com.andreidodu.fromgtog.gui.validator.composite.factory;

import com.andreidodu.fromgtog.gui.validator.composite.ComponentValidator;
import com.andreidodu.fromgtog.gui.validator.composite.PrimaryComponentValidator;
import com.andreidodu.fromgtog.gui.validator.composite.SecondaryComponentValidator;
import com.andreidodu.fromgtog.type.EngineOptionsType;
import com.andreidodu.fromgtog.type.EngineType;
import org.json.JSONObject;

import java.util.function.Predicate;

import static com.andreidodu.fromgtog.gui.controller.constants.GuiKeys.*;
import static com.andreidodu.fromgtog.gui.validator.composite.factory.util.FactoryUtil.*;

public class FromGiteaTabFilterTabFactory implements ComponentValidatorFactory {
    @Override
    public ComponentValidator createValidator() {
        Predicate<JSONObject> mainTabPredicate = isFromMainTabIndexEqualTo(EngineType.GITEA);
        Predicate<JSONObject> secondaryTabPredicate = isSecondaryTabIndexEqualTo(EngineOptionsType.FILTER, FROM_GITEA_OPTIONS_TABBED_PANE_INDEX);
        Predicate<JSONObject> mainIsApplicableCondition = mainTabPredicate.and(secondaryTabPredicate);

        PrimaryComponentValidator primaryTab = new PrimaryComponentValidator(mainIsApplicableCondition);

        primaryTab.addComponentValidator(new SecondaryComponentValidator(mainIsApplicableCondition, isMandatoryTokenValid(FROM_GITEA_TOKEN), "token"));
        primaryTab.addComponentValidator(new SecondaryComponentValidator(mainIsApplicableCondition, isMandatoryUrlValid(FROM_GITEA_URL), "URL"));

        buildGiteaFilterTab(mainIsApplicableCondition, primaryTab);

        return primaryTab;
    }


}
