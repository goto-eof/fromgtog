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
public class ToGiteaController implements DataProviderToController {

    private JTextField toGiteaUrlTextField;
    private JTextField toGiteaTokenTextField;
    private JComboBox toGiteaPrivacyComboBox;

    final static int TAB_INDEX = EngineType.GITEA.getValue();

    public ToGiteaController(JTextField toGiteaUrlTextField, JTextField toGiteaTokenTextField, JComboBox toGiteaPrivacyComboBox) {
        this.toGiteaUrlTextField = toGiteaUrlTextField;
        this.toGiteaTokenTextField = toGiteaTokenTextField;
        this.toGiteaPrivacyComboBox = toGiteaPrivacyComboBox;

        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(new String[]{"All private", "All public"});
        this.toGiteaPrivacyComboBox.setModel(model);
    }

    @Override
    public boolean accept(int tabIndex) {
        return TAB_INDEX == tabIndex;
    }

    @Override
    public JSONObject getDataFromChildren() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(TO_GITEA_URL, toGiteaUrlTextField.getText());
        jsonObject.put(TO_GITEA_TOKEN, toGiteaTokenTextField.getText());
        jsonObject.put(TO_GITEA_PRIVACY_INDEX, toGiteaPrivacyComboBox.getSelectedIndex());
        jsonObject.put(ENGINE_TYPE, EngineType.fromValue(TAB_INDEX));

        return jsonObject;
    }
}
