package com.andreidodu.fromgtog.gui.controller;

import org.json.JSONObject;

public interface GUIController {

    void applySettings(JSONObject settings);

    JSONObject getDataFromChildren();
}
