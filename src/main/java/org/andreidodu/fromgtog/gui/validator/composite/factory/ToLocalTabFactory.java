package org.andreidodu.fromgtog.gui.validator.composite.factory;

import org.andreidodu.fromgtog.gui.validator.composite.ComponentValidator;
import org.andreidodu.fromgtog.gui.validator.composite.PrimaryComponentValidator;
import org.andreidodu.fromgtog.gui.validator.composite.SecondaryComponentValidator;
import org.andreidodu.fromgtog.type.EngineType;
import org.json.JSONObject;

import java.util.function.Predicate;

import static org.andreidodu.fromgtog.gui.controller.constants.GuiKeys.TO_LOCAL_ROOT_PATH;
import static org.andreidodu.fromgtog.gui.validator.composite.factory.util.FactoryUtil.isMandatoryDirPathValid;
import static org.andreidodu.fromgtog.gui.validator.composite.factory.util.FactoryUtil.isToMainTabIndexEqualTo;

public class ToLocalTabFactory implements ComponentValidatorFactory {
    @Override
    public ComponentValidator createValidator() {
        Predicate<JSONObject> mainIsApplicableCondition = isToMainTabIndexEqualTo(EngineType.LOCAL);

        PrimaryComponentValidator primaryTab = new PrimaryComponentValidator(mainIsApplicableCondition);

        primaryTab.addComponentValidator(new SecondaryComponentValidator(mainIsApplicableCondition, isMandatoryDirPathValid(TO_LOCAL_ROOT_PATH), "directory path"));

        return primaryTab;
    }


}
