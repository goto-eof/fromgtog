package com.andreidodu.fromgtog.gui.controller.impl;

import com.andreidodu.fromgtog.gui.controller.GUIToController;
import com.andreidodu.fromgtog.type.EngineType;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import javax.swing.*;

import static com.andreidodu.fromgtog.gui.controller.constants.GuiKeys.*;

@Getter
@Setter
public class ToGithubController implements GUIToController {

    final static int TAB_INDEX = EngineType.GITHUB.getValue();
    private JTextField toGithubTokenTextField;
    private JComboBox toGithubPrivacyComboBox;

    public ToGithubController(JSONObject settings, JTextField toGithubTokenTextField, JComboBox toGithubPrivacyComboBox) {
        this.toGithubTokenTextField = toGithubTokenTextField;
        this.toGithubPrivacyComboBox = toGithubPrivacyComboBox;

        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(new String[]{"All private", "All public"});
        this.toGithubPrivacyComboBox.setModel(model);

        applySettings(settings);
    }

    @Override
    public void applySettings(JSONObject settings) {
        toGithubTokenTextField.setText(settings.optString(TO_GITHUB_TOKEN));
        toGithubPrivacyComboBox.setSelectedIndex(settings.optInt(TO_GITHUB_PRIVACY_INDEX));
    }

    @Override
    public boolean accept(int tabIndex) {
        return TAB_INDEX == tabIndex;
    }

    @Override
    public JSONObject getDataFromChildren() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(TO_GITHUB_TOKEN, toGithubTokenTextField.getText());
        jsonObject.put(TO_GITHUB_PRIVACY_INDEX, toGithubPrivacyComboBox.getSelectedIndex());
        jsonObject.put(ENGINE_TYPE, EngineType.fromValue(TAB_INDEX));

        return jsonObject;
    }
}
