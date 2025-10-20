package org.andreidodu.fromgtog.service;

import org.json.JSONObject;

public interface SettingsService {
    void save(JSONObject json);

    JSONObject load();
}
