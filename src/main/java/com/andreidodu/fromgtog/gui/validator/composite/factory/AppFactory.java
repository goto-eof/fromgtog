package com.andreidodu.fromgtog.gui.validator.composite.factory;

import com.andreidodu.fromgtog.gui.validator.composite.ComponentValidator;
import com.andreidodu.fromgtog.gui.validator.composite.PrimaryComponentValidator;
import com.andreidodu.fromgtog.gui.validator.composite.SecondaryComponentValidator;

import static com.andreidodu.fromgtog.gui.controller.constants.GuiKeys.APP_SLEEP_TIME;
import static com.andreidodu.fromgtog.gui.validator.composite.factory.util.FactoryUtil.isMandatorySleepTimeValid;

public class AppFactory implements ComponentValidatorFactory {
    @Override
    public ComponentValidator createValidator() {
        PrimaryComponentValidator primaryTab = new PrimaryComponentValidator((json) -> true);

        primaryTab.addComponentValidator(new SecondaryComponentValidator((json) -> true, isMandatorySleepTimeValid(APP_SLEEP_TIME), "app sleep time"));

        return primaryTab;
    }


}
