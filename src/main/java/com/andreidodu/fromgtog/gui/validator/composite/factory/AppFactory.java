package com.andreidodu.fromgtog.gui.validator.composite.factory;

import com.andreidodu.fromgtog.gui.validator.composite.ComponentValidator;
import com.andreidodu.fromgtog.gui.validator.composite.PrimaryComponentValidator;
import com.andreidodu.fromgtog.gui.validator.composite.SecondaryComponentValidator;

import static com.andreidodu.fromgtog.gui.controller.constants.GuiKeys.*;
import static com.andreidodu.fromgtog.gui.validator.composite.factory.util.FactoryUtil.*;

public class AppFactory implements ComponentValidatorFactory {
    @Override
    public ComponentValidator createValidator() {
        PrimaryComponentValidator primaryTab = new PrimaryComponentValidator((json) -> true);

        primaryTab.addComponentValidator(new SecondaryComponentValidator((json) -> true, isMandatorySleepTimeValid(APP_SLEEP_TIME), "app sleep time"));
        primaryTab.addComponentValidator(new SecondaryComponentValidator(jsonObject -> true, (jsonObject) -> {
            if (isTrue(APP_CHRON_JOB_ENABLED).test(jsonObject)) {
                return isChronExpressionValid(APP_CHRON_JOB_EXPRESSION)
                        .test(jsonObject);
            }
            return true;
        }, "chron expression"));
        return primaryTab;
    }


}
