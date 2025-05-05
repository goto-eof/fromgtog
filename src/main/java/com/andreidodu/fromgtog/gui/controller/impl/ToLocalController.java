package com.andreidodu.fromgtog.gui.controller.impl;

import com.andreidodu.fromgtog.gui.controller.DataProviderToController;
import com.andreidodu.fromgtog.type.EngineType;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import javax.swing.*;

import static com.andreidodu.fromgtog.gui.GuiKeys.*;

@Getter
@Setter
public class ToLocalController implements DataProviderToController {

    private JTextField toLocalRootPathTextField;
    private JCheckBox toLocalGroupByRepositoryOwnerCheckBox;
    final static int TAB_INDEX = EngineType.LOCAL.getValue();

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
        jsonObject.put(ENGINE_TYPE, EngineType.fromValue(TAB_INDEX));

        return jsonObject;
    }
}
