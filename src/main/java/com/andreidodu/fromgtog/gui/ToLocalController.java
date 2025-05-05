package com.andreidodu.fromgtog.gui;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import javax.swing.*;

import static com.andreidodu.fromgtog.gui.GuiKeys.TO_LOCAL_GROUP_BY_OWNER;
import static com.andreidodu.fromgtog.gui.GuiKeys.TO_LOCAL_ROOT_PATH;

@Getter
@Setter
public class ToLocalController implements DataProviderToController {

    private JTextField toLocalRootPathTextField;
    private JCheckBox toLocalGroupByRepositoryOwnerCheckBox;
    final static int TAB_INDEX = 3;

    public ToLocalController(JTextField toLocalRootPathTextField, JCheckBox toLocalGroupByRepositoryOwnerCheckBox) {
        this.toLocalRootPathTextField = toLocalRootPathTextField;
        this.toLocalGroupByRepositoryOwnerCheckBox = toLocalGroupByRepositoryOwnerCheckBox;
    }

    @Override
    public boolean accept(int tabIndex) {
        return TAB_INDEX == tabIndex;
    }

    @Override
    public JSONObject getDataFromChildren() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(TO_LOCAL_ROOT_PATH, toLocalRootPathTextField.getText());
        jsonObject.put(TO_LOCAL_GROUP_BY_OWNER, toLocalGroupByRepositoryOwnerCheckBox.getLocale());
        return jsonObject;
    }
}
