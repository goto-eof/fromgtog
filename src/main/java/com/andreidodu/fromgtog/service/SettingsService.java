package com.andreidodu.fromgtog.service;

import java.util.Map;

public interface SettingsService {
    void save(Map<String, String> map);

    Map<String, String> load();
}
