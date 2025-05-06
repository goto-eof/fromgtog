package com.andreidodu.fromgtog.gui.controller.impl;

import com.andreidodu.fromgtog.gui.controller.DataProviderFromController;
import com.andreidodu.fromgtog.type.EngineType;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import javax.swing.*;

import static com.andreidodu.fromgtog.gui.GuiKeys.*;

@Getter
@Setter
public class FromLocalController implements DataProviderFromController {
    private JTextField fromLocalRootPathTextField;

    private final static int TAB_INDEX = EngineType.LOCAL.getValue();

    public FromLocalController(JSONObject settings, JTextField fromLocalRootPathTextField) {
        this.fromLocalRootPathTextField = fromLocalRootPathTextField;

        applySettings(settings);
    }

    private void applySettings(JSONObject settings) {
        fromLocalRootPathTextField.setText(settings.optString(FROM_LOCAL_ROOT_PATH));

    }

    @Override
    public boolean accept(int tabIndex) {
        return TAB_INDEX == tabIndex;
    }

    @Override
    public JSONObject getDataFromChildren() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(FROM_LOCAL_ROOT_PATH, fromLocalRootPathTextField.getText());
        jsonObject.put(ENGINE_TYPE, EngineType.fromValue(TAB_INDEX));

        return jsonObject;
    }
}
