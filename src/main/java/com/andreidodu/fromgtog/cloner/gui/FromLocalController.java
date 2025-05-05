package com.andreidodu.fromgtog.cloner.gui;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import javax.swing.*;

@Getter
@Setter
public class FromLocalController implements DataProviderFromController {
    private JTextField fromLocalRootPathTextField;

    private final static int TAB_INDEX = 3;

    public FromLocalController(JTextField fromLocalRootPathTextField) {
        this.fromLocalRootPathTextField = fromLocalRootPathTextField;
    }

    @Override
    public boolean accept(int tabIndex) {
        return TAB_INDEX == tabIndex;
    }

    @Override
    public JSONObject getDataFromChildren() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("from.local.root-path", fromLocalRootPathTextField.getText());

        return jsonObject;
    }
}
