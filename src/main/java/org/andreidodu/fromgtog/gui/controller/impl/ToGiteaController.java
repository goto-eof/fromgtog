package org.andreidodu.fromgtog.gui.controller.impl;

import org.andreidodu.fromgtog.gui.controller.GUIToController;
import org.andreidodu.fromgtog.type.EngineType;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import javax.swing.*;

import static org.andreidodu.fromgtog.gui.controller.constants.GuiKeys.*;

@Getter
@Setter
public class ToGiteaController implements GUIToController {

    final static int TAB_INDEX = EngineType.GITEA.getValue();
    private JTextField toGiteaUrlTextField;
    private JTextField toGiteaTokenTextField;
    private JComboBox toGiteaPrivacyComboBox;
    private JCheckBox toGiteaOverrideIfExistsCheckBox;

    public ToGiteaController(JSONObject settings, JTextField toGiteaUrlTextField, JTextField toGiteaTokenTextField, JComboBox toGiteaPrivacyComboBox, JCheckBox toGiteaOverrideIfExistsCheckBox) {
        this.toGiteaUrlTextField = toGiteaUrlTextField;
        this.toGiteaTokenTextField = toGiteaTokenTextField;
        this.toGiteaPrivacyComboBox = toGiteaPrivacyComboBox;
        this.toGiteaOverrideIfExistsCheckBox = toGiteaOverrideIfExistsCheckBox;

        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(new String[]{"All private", "All public"});
        this.toGiteaPrivacyComboBox.setModel(model);

        applySettings(settings);
    }

    @Override
    public void applySettings(JSONObject settings) {
        toGiteaUrlTextField.setText(settings.optString(TO_GITEA_URL));
        toGiteaTokenTextField.setText(settings.optString(TO_GITEA_TOKEN));
        toGiteaPrivacyComboBox.setSelectedIndex(settings.optInt(TO_GITEA_PRIVACY_INDEX));

        toGiteaOverrideIfExistsCheckBox.setSelected(settings.optBoolean(TO_GITEA_OVERRIDE_IF_EXISTS, false));
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

        jsonObject.put(TO_GITEA_OVERRIDE_IF_EXISTS, toGiteaOverrideIfExistsCheckBox.isSelected());

        return jsonObject;
    }
}
