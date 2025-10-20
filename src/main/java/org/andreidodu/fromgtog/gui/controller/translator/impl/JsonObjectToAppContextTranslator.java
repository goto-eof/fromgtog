package org.andreidodu.fromgtog.gui.controller.translator.impl;

import org.andreidodu.fromgtog.dto.SettingsContext;
import org.andreidodu.fromgtog.gui.controller.translator.JsonObjectToRecordTranslator;
import org.json.JSONException;
import org.json.JSONObject;

import static org.andreidodu.fromgtog.gui.controller.constants.GuiKeys.*;

public class JsonObjectToAppContextTranslator implements JsonObjectToRecordTranslator<SettingsContext> {
    @Override
    public SettingsContext translate(JSONObject jsonObject) throws JSONException {
        return new SettingsContext(
                jsonObject.getInt(APP_SLEEP_TIME),
                jsonObject.getBoolean(APP_MULTITHREADING_ENABLED),
                jsonObject.getBoolean(APP_CHRON_JOB_ENABLED),
                jsonObject.getString(APP_CHRON_JOB_EXPRESSION)
        );
    }
}
