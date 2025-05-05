package com.andreidodu.fromgtog.cloner.gui;

import org.json.JSONObject;

public interface DataProviderController {

    boolean accept(int tabIndex);

    JSONObject getDataFromChildren();
}
