package org.andreidodu.fromgtog.gui.validator.composite.factory;

import org.andreidodu.fromgtog.gui.validator.composite.ComponentValidator;
import org.andreidodu.fromgtog.gui.validator.composite.PrimaryComponentValidator;
import org.andreidodu.fromgtog.gui.validator.composite.SecondaryComponentValidator;
import org.andreidodu.fromgtog.type.EngineType;
import org.json.JSONObject;

import java.util.function.Predicate;

import static org.andreidodu.fromgtog.gui.controller.constants.GuiKeys.FROM_LOCAL_ROOT_PATH;
import static org.andreidodu.fromgtog.gui.validator.composite.factory.util.FactoryUtil.isFromMainTabIndexEqualTo;
import static org.andreidodu.fromgtog.gui.validator.composite.factory.util.FactoryUtil.isMandatoryDirPathValid;

public class FromLocalTabFactory implements ComponentValidatorFactory {
    @Override
    public ComponentValidator createValidator() {
        Predicate<JSONObject> mainIsApplicableCondition = isFromMainTabIndexEqualTo(EngineType.LOCAL);

        PrimaryComponentValidator primaryTab = new PrimaryComponentValidator(mainIsApplicableCondition);

        primaryTab.addComponentValidator(new SecondaryComponentValidator(mainIsApplicableCondition, isMandatoryDirPathValid(FROM_LOCAL_ROOT_PATH), "directory path"));

        return primaryTab;
    }


}
