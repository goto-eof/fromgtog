package com.andreidodu.fromgtog.service;

import org.json.JSONObject;

import java.util.Map;

public interface SettingsService {
    void save(JSONObject json);

    JSONObject load();
}
