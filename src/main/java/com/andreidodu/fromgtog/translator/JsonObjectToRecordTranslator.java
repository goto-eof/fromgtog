package com.andreidodu.fromgtog.translator;

import org.json.JSONException;
import org.json.JSONObject;

public interface JsonObjectToRecordTranslator<T> {

    T translate(JSONObject jsonObject) throws JSONException;

}
