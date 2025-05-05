package com.andreidodu.fromgtog.util;

import org.json.JSONObject;

public class JsonObjectUtil {

    public JSONObject merge(JSONObject json1, JSONObject json2) {
        JSONObject jsonObject = new JSONObject();
        copyFromTo(json1, jsonObject);
        copyFromTo(json2, jsonObject);
        return jsonObject;
    }

    private static void copyFromTo(JSONObject json2, JSONObject jsonObject) {
        json2.keySet().forEach(key2 -> jsonObject.put(key2, json2.get(key2)));
    }
}
