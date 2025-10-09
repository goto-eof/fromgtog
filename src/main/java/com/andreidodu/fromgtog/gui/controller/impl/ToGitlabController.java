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
public class ToGitlabController implements GUIToController {

    final static int TAB_INDEX = EngineType.GITLAB.getValue();
    private JTextField toGitlabUrlTextField;
    private JTextField toGitlabTokenTextField;
    private JComboBox toGitlabPrivacyComboBox;

    public ToGitlabController(JSONObject settings, JTextField toGitlabUrlTextField, JTextField toGitlabTokenTextField, JComboBox toGitlabPrivacyComboBox) {
        this.toGitlabUrlTextField = toGitlabUrlTextField;
        this.toGitlabTokenTextField = toGitlabTokenTextField;
        this.toGitlabPrivacyComboBox = toGitlabPrivacyComboBox;

        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(new String[]{"All private", "All public"});
        this.toGitlabPrivacyComboBox.setModel(model);

        applySettings(settings);
    }

    @Override
    public void applySettings(JSONObject settings) {
        toGitlabUrlTextField.setText(settings.optString(TO_GITLAB_URL));
        toGitlabTokenTextField.setText(settings.optString(TO_GITLAB_TOKEN));
        toGitlabPrivacyComboBox.setSelectedIndex(settings.optInt(TO_GITLAB_PRIVACY_INDEX));
    }

    @Override
    public boolean accept(int tabIndex) {
        return TAB_INDEX == tabIndex;
    }

    @Override
    public JSONObject getDataFromChildren() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(TO_GITLAB_URL, toGitlabUrlTextField.getText());
        jsonObject.put(TO_GITLAB_TOKEN, toGitlabTokenTextField.getText());
        jsonObject.put(TO_GITLAB_PRIVACY_INDEX, toGitlabPrivacyComboBox.getSelectedIndex());
        jsonObject.put(ENGINE_TYPE, EngineType.fromValue(TAB_INDEX));

        return jsonObject;
    }
}
