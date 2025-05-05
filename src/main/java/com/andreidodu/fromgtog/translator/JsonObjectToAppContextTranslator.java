package com.andreidodu.fromgtog.translator;

import com.andreidodu.fromgtog.dto.SettingsContext;
import com.andreidodu.fromgtog.dto.ToContext;
import org.json.JSONException;
import org.json.JSONObject;

import static com.andreidodu.fromgtog.gui.GuiKeys.APP_SLEEP_TIME;

public class JsonObjectToAppContextTranslator implements JsonObjectToRecordTranslator<SettingsContext> {
    @Override
    public SettingsContext translate(JSONObject jsonObject) throws JSONException {
        return new SettingsContext(
                jsonObject.getInt(APP_SLEEP_TIME)
        );
    }
}
