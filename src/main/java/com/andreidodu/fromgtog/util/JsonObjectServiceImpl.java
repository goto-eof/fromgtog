package com.andreidodu.fromgtog.util;

import org.json.JSONObject;

import java.util.Arrays;

public class JsonObjectServiceImpl {

    private static JsonObjectServiceImpl instance;

    public static JsonObjectServiceImpl getInstance() {
        if (instance == null) {
            instance = new JsonObjectServiceImpl();
        }
        return instance;
    }

    public JSONObject merge(JSONObject json1, JSONObject json2) {
        JSONObject jsonObject = new JSONObject();
        copyFromTo(json1, jsonObject);
        copyFromTo(json2, jsonObject);
        return jsonObject;
    }

    public JSONObject merge(JSONObject... json) {
        JSONObject jsonObject = new JSONObject();
        Arrays.stream(json)
                .forEach(source -> copyFromTo(source, jsonObject));
        return jsonObject;
    }

    private static void copyFromTo(JSONObject jsonFrom, JSONObject jsonTo) {
        jsonFrom.keySet().forEach(keyFrom -> jsonTo.put(keyFrom, jsonFrom.get(keyFrom)));
    }
}
