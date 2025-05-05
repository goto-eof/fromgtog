package com.andreidodu.fromgtog.gui;

import org.json.JSONObject;

public interface DataProviderController {

    boolean accept(int tabIndex);

    JSONObject getDataFromChildren();
}
