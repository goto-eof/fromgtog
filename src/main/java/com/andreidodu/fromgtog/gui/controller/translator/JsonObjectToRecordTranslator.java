package com.andreidodu.fromgtog.gui.controller.translator;

import org.json.JSONException;
import org.json.JSONObject;

public interface JsonObjectToRecordTranslator<T> {

    T translate(JSONObject jsonObject) throws JSONException;

}
